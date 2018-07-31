package a3.com.convo.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.List;

import a3.com.convo.Models.Page;
import a3.com.convo.R;

public class AdditionalLikeAdapter extends RecyclerView.Adapter<AdditionalLikeAdapter.ViewHolder> {
    private List <String> mLikes;
    Context context;
    private RecyclerViewItemClickListener recyclerViewItemClickListener;

    public AdditionalLikeAdapter(List<String> likes) {
        mLikes = likes;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View likeView = inflater.inflate(R.layout.item_like, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(likeView);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        viewHolder.position = i;
        String likeObjectId = (String) mLikes.get(i);
        if (likeObjectId == null) {
            Log.e("LikeAdapter", "likeObjectId is null");
            return;
        }
        else {
            ParseQuery<Page> query = ParseQuery.getQuery(Page.class);
            query.getInBackground(likeObjectId, new GetCallback<Page>() {
                @Override
                public void done(Page object, ParseException e) {
                    if (e == null && object != null) {
                        String like_name = object.getName();
                        if (like_name == null || like_name.isEmpty()) {
                            Log.e("LikeAdapter", "User's hometown object has no name");
                            return;
                        }
                        viewHolder.tvLike.setText(like_name);
                    }
                    else if (object == null){
                        Log.e("likeAdapter", "like name not showing up");
                    }
                    else {
                        Log.e("likeAdapter", "error e");
                        e.printStackTrace();
                    }
                }
            });
        }
        //viewHolder.tvLike.setText(likeString);
    }

    //Set method of OnItemLongClickListener object
    public void setOnItemLongClickListener(RecyclerViewItemClickListener recyclerViewItemClickListener){
        this.recyclerViewItemClickListener = recyclerViewItemClickListener;
    }

    public int getItemCount() {
        return mLikes.size();
    }

    public void clear() {
        mLikes.clear();
        notifyDataSetChanged();
    }

    public void add(String like) {
        mLikes.add(like);
        notifyItemInserted(0);
    }

    public void delete(int position) {
        mLikes.remove(position);
        notifyItemRemoved(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView tvLike;
        public int position;
        public ViewHolder(View itemView) {
            super(itemView);
            tvLike = (TextView) itemView.findViewById(R.id.tv_liked_item);

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    //When item view is clicked long, trigger the itemclicklistener
                    //Because that itemclicklistener is indicated in MainActivity
                    recyclerViewItemClickListener.onItemLongClick(v,position);
                    return true;
                }
            });

        }

        @Override
        public void onClick(View view) {

        }
    }
}
