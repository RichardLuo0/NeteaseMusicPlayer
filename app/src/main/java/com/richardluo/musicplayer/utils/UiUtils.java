package com.richardluo.musicplayer.utils;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
import static android.view.View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
import static android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;

public class UiUtils {
    public abstract static class Bindable<T, U> {
        protected abstract T onCreateViewHolder(@NonNull ViewGroup parent, int viewType);

        protected abstract void bind(@NonNull T holder, U object);

        protected void onUpdate() {
        }
    }

    public interface Identifiable {
        int getId();
    }

    public static <T extends RecyclerView.ViewHolder, U extends Identifiable> void bindRecyclerViewViewWithLiveData(LifecycleOwner owner, RecyclerView view, LiveData<List<U>> liveData, Bindable<T, U> bindable) {
        RecyclerView.Adapter<T> adapter = new RecyclerView.Adapter<T>() {

            @NonNull
            @Override
            public T onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return bindable.onCreateViewHolder(parent, viewType);
            }

            @Override
            public void onBindViewHolder(@NonNull T holder, int position) {
                List<U> list = liveData.getValue();
                if (list != null)
                    bindable.bind(holder, list.get(position));
            }

            @Override
            public int getItemCount() {
                List<U> list = liveData.getValue();
                return list != null ? list.size() : 0;
            }

            @Override
            public long getItemId(int position) {
                List<U> list = liveData.getValue();
                return list != null ? list.get(position).getId() : 0;
            }
        };
        adapter.setHasStableIds(true);
        view.setAdapter(adapter);
        liveData.observe(owner, list -> {
            adapter.notifyDataSetChanged();
            bindable.onUpdate();
        });
    }

    public enum SystemPadding {
        LEFT, TOP, RIGHT, BOTTOM, NULL
    }

    public static void setSystemPadding(View view, SystemPadding systemPadding) {
        View.OnApplyWindowInsetsListener listener;
        switch (systemPadding) {
            case LEFT:
                listener = (view1, inserts) -> {
                    view1.setPadding(inserts.getSystemWindowInsetLeft(), 0, 0, 0);
                    return inserts;
                };
                break;
            case TOP:
                listener = (view1, inserts) -> {
                    view1.setPadding(0, inserts.getSystemWindowInsetTop(), 0, 0);
                    return inserts;
                };
                break;
            case RIGHT:
                listener = (view1, inserts) -> {
                    view1.setPadding(0, 0, inserts.getSystemWindowInsetRight(), 0);
                    return inserts;
                };
                break;
            case BOTTOM:
                listener = (view1, inserts) -> {
                    view1.setPadding(0, 0, 0, inserts.getSystemWindowInsetBottom());
                    return inserts;
                };
                break;
            case NULL:
                listener = (view1, inserts) -> {
                    view1.setPadding(0, 0, 0, 0);
                    return inserts;
                };
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + systemPadding);
        }
        view.setOnApplyWindowInsetsListener(listener);
    }

    public static void setWidth(View view, int value) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.width = value;
        view.setLayoutParams(params);
    }

    public static void setHeight(Context context,View view, int value) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.height = (int) (value * context.getResources().getDisplayMetrics().density);
        view.setLayoutParams(params);
    }
}
