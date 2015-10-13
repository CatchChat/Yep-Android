package catchla.yep.view.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import catchla.yep.R;
import catchla.yep.adapter.GithubUserAdapter;
import catchla.yep.model.GithubRepo;
import catchla.yep.model.GithubUser;
import catchla.yep.model.GithubUserInfo;
import catchla.yep.util.ImageLoaderWrapper;

/**
 * Created by mariotaku on 15/6/4.
 */
public class GithubUserHeaderViewHolder extends RecyclerView.ViewHolder {
    private final ImageView profileImageView;
    private final TextView followersCount;
    private final TextView starsCount;
    private final TextView followingCount;
    private final GithubUserAdapter adapter;

    public GithubUserHeaderViewHolder(final View itemView, final GithubUserAdapter adapter) {
        super(itemView);
        this.adapter = adapter;
        profileImageView = (ImageView) itemView.findViewById(R.id.profile_image);
        followersCount = (TextView) itemView.findViewById(R.id.followers_count);
        starsCount = (TextView) itemView.findViewById(R.id.stars_count);
        followingCount = (TextView) itemView.findViewById(R.id.following_count);

    }

    public void displayUser(final GithubUserInfo userInfo) {
        final GithubUser user = userInfo.getUser();
        final String avatarUrl = user.getAvatarUrl();
        final ImageLoaderWrapper imageLoader = adapter.getImageLoader();
        imageLoader.displayProfileImage(avatarUrl, profileImageView);
        followingCount.setText(String.valueOf(user.getFollowing()));
        followersCount.setText(String.valueOf(user.getFollowers()));
        starsCount.setText(String.valueOf(getStarsSum(userInfo)));
    }

    private long getStarsSum(final GithubUserInfo userInfo) {
        long stars = 0;
        for (GithubRepo repo : userInfo.getRepos()) {
            stars = stars + repo.getStargazersCount();
        }
        return stars;
    }
}
