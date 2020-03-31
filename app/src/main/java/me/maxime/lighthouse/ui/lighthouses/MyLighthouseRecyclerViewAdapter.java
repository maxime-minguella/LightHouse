package me.maxime.lighthouse.ui.lighthouses;

import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import me.maxime.lighthouse.MainActivity;
import me.maxime.lighthouse.R;
import me.maxime.lighthouse.ui.lighthouses.LighthouseFragment.OnListFragmentInteractionListener;
import me.maxime.lighthouse.ui.lighthouses.phare.LighthouseContent;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link LighthouseContent.LighthouseItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyLighthouseRecyclerViewAdapter extends RecyclerView.Adapter<MyLighthouseRecyclerViewAdapter.ViewHolder> {

    private final List<LighthouseContent.LighthouseItem> mValues;
    private final OnListFragmentInteractionListener mListener;

    public MyLighthouseRecyclerViewAdapter(List<LighthouseContent.LighthouseItem> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_lighthouse, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mNameView.setText(mValues.get(position).name);
        holder.mRegionView.setText(mValues.get(position).region);
        holder.mConstructionView.setText(String.valueOf(mValues.get(position).construction));

        Context context = MainActivity.getContext();

        int i = context.getResources().getIdentifier(this.mValues.get(position).imgFile, "drawable", context.getPackageName());

        Log.d("MyLighthouseRecyclerV", this.mValues.get(position).imgFile + ", imdID: " + i);

        holder.mImageView.setImageResource(i);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mNameView;
        public final TextView mRegionView;
        public final TextView mConstructionView;
        public final ImageView mImageView;
        public LighthouseContent.LighthouseItem mItem;

        public ViewHolder(View view) {
            super(view);
            this.mView = view;
            this.mNameView = view.findViewById(R.id.item_name);
            this.mRegionView = view.findViewById(R.id.item_region);
            this.mConstructionView = view.findViewById(R.id.item_construction);
            this.mImageView = view.findViewById(R.id.item_list_img);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mRegionView.getText() + "'";
        }
    }
}
