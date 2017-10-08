package com.soussidev.kotlin.retrofit_get_post.adapter;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.soussidev.kotlin.retrofit_get_post.R;
import com.soussidev.kotlin.retrofit_get_post.model.User;

import java.util.List;

import static com.soussidev.kotlin.retrofit_get_post.ListActivity.SPAN_COUNT_ONE;
import static com.soussidev.kotlin.retrofit_get_post.MethodeGet.Constants.BASE_URL;


/**
 * Created by Soussi on 06/10/2017.
 */

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ItemViewHolder>{

    private static final int VIEW_TYPE_SMALL = 1;
    private static final int VIEW_TYPE_BIG = 2;

    private List<User> mUsers;
    private GridLayoutManager mLayoutManager;
    private Context mcontext;

    public UserAdapter(List<User> items, GridLayoutManager layoutManager,Context context) {
        mUsers = items;
        mLayoutManager = layoutManager;
        mcontext = context;
    }

    @Override
    public int getItemViewType(int position) {
        int spanCount = mLayoutManager.getSpanCount();
        if (spanCount == SPAN_COUNT_ONE) {
            return VIEW_TYPE_BIG;
        } else {
            return VIEW_TYPE_SMALL;
        }
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_BIG) {//parent.getContext()
            view = LayoutInflater.from(mcontext).inflate(R.layout.item_big, parent, false);
        } else {
            view = LayoutInflater.from(mcontext).inflate(R.layout.item_small, parent, false);
        }
        return new ItemViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {

        // final Item item = mItems.get(position % 4);
        final User user = mUsers.get(position );
        holder.title.setText(user.getNomUser());
        Glide.with(mcontext)
               .load(BASE_URL+user.getImgUser())
                .apply(RequestOptions.circleCropTransform())
                .into(holder.iv);

      //  holder.iv.setImageResource(R.mipmap.ic_launcher);
        if (getItemViewType(position) == VIEW_TYPE_BIG) {
            holder.info.setText(user.getNomUser() + " likes  ·  " + user.getPrenomUser() + " comments");
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mcontext,String.valueOf(user.getCinUser()), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {

        return mUsers.size();
    }




    class ItemViewHolder extends RecyclerView.ViewHolder {
        ImageView iv;
        TextView title;
        TextView info;

        ItemViewHolder(View itemView, int viewType) {
            super(itemView);
            if (viewType == VIEW_TYPE_BIG) {
                iv = (ImageView) itemView.findViewById(R.id.image_big);
                title = (TextView) itemView.findViewById(R.id.title_big);
                info = (TextView) itemView.findViewById(R.id.tv_info);
            } else {
                iv = (ImageView) itemView.findViewById(R.id.image_small);
                title = (TextView) itemView.findViewById(R.id.title_small);
            }

           /* itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos =getPosition();
                    Toast.makeText(mcontext,String.valueOf(pos), Toast.LENGTH_SHORT).show();
                }
            });*/

        }



    }



}
