package catchla.yep.view.holder;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import catchla.yep.R;
import catchla.yep.model.GithubRepo;

/**
 * Created by mariotaku on 15/6/4.
 */
public class GithubRepoItemViewHolder extends RecyclerView.ViewHolder {

    private final TextView repoNameView;
    private final TextView starCountView;
    private final TextView descriptionView;

    public GithubRepoItemViewHolder(final View itemView) {
        super(itemView);
        repoNameView = (TextView) itemView.findViewById(R.id.repo_name);
        starCountView = (TextView) itemView.findViewById(R.id.stars_count);
        descriptionView = (TextView) itemView.findViewById(R.id.description);
    }

    public void displayRepo(final GithubRepo repo) {
        repoNameView.setText(repo.getName());
        starCountView.setText(String.valueOf(repo.getStargazersCount()));
        descriptionView.setText(repo.getDescription());
        descriptionView.setVisibility(TextUtils.isEmpty(repo.getDescription()) ? View.GONE : View.VISIBLE);
    }
}
