package com.example.android.popularmovies.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import com.example.android.popularmovies.api.ApiResponse;
import com.example.android.popularmovies.data.Resource;
import com.example.android.popularmovies.utils.AppExecutor;

import java.util.Objects;

public abstract class NetworkBoundResource<ResultType, RequestType> {

    private final AppExecutor appExecutor;

    private final MediatorLiveData<Resource<ResultType>> result = new MediatorLiveData<>();

    @MainThread
    NetworkBoundResource(final AppExecutor appExecutor) {
        this.appExecutor = appExecutor;
        result.setValue(Resource.loading((ResultType) null));
        final LiveData<ResultType> dbSource = loadFromDB();
        result.addSource(dbSource, new Observer<ResultType>() {
            @Override
            public void onChanged(@Nullable final ResultType data) {
                result.removeSource(dbSource);
                if (shouldFetch(data)) {
                    fetchFromNetwork(dbSource);
                } else {
                    result.addSource(dbSource, new Observer<ResultType>() {
                        @Override
                        public void onChanged(@Nullable ResultType newData) {
                            setValue(Resource.success(newData));
                        }
                    });
                }

            }
        });
    }

    @MainThread
    private void setValue(Resource<ResultType> newValue) {
        if (!Objects.equals(result.getValue(), newValue)) {
            result.setValue(newValue);
        }
    }

    private void fetchFromNetwork(final LiveData<ResultType> dbSource) {
        final LiveData<ApiResponse<RequestType>> apiResponse = createCall();
        result.addSource(dbSource, new Observer<ResultType>() {
            @Override
            public void onChanged(@Nullable ResultType newData) {
                setValue(Resource.loading(newData));

            }
        });
        result.addSource(apiResponse, new Observer<ApiResponse<RequestType>>() {
            @Override
            public void onChanged(@Nullable final ApiResponse<RequestType> response) {
                result.removeSource(apiResponse);
                result.removeSource(dbSource);
                if (response.isSuccessful()) {
                    appExecutor.diskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            saveCallResult(processResponse(response));
                            appExecutor.mainThread().execute(new Runnable() {
                                @Override
                                public void run() {
                                    result.addSource(loadFromDB(), new Observer<ResultType>() {
                                        @Override
                                        public void onChanged(@Nullable ResultType newData) {
                                            setValue(Resource.success(newData));
                                        }
                                    });
                                }
                            });
                        }
                    });
                } else {
                    onFetchFailed();
                    result.addSource(dbSource, new Observer<ResultType>() {
                        @Override
                        public void onChanged(@Nullable ResultType newData) {
                            setValue(Resource.error(response.errorMessage, newData));
                        }
                    });
                }
            }
        });
    }

    public final LiveData<Resource<ResultType>> getAsLiveData() {
        return result;
    }

    @WorkerThread
    protected RequestType processResponse(ApiResponse<RequestType> response) {
        return response.body;
    }

    @WorkerThread
    protected abstract void saveCallResult(@NonNull RequestType item);

    @MainThread
    protected abstract boolean shouldFetch(@Nullable ResultType data);

    @NonNull
    @MainThread
    protected abstract LiveData<ResultType> loadFromDB();

    @NonNull
    @MainThread
    protected abstract LiveData<ApiResponse<RequestType>> createCall();

    @MainThread
    protected void onFetchFailed() {
    }


}
