package catchla.yep.view.holder;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import catchla.yep.R;
import catchla.yep.adapter.GithubUserAdapter;
import catchla.yep.model.GithubRepo;

/**
 * Created by mariotaku on 15/6/4.
 */
public class GithubRepoItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private final GithubUserAdapter adapter;

    private final TextView repoNameView;
    private final TextView starCountView;
    private final TextView descriptionView;

    public GithubRepoItemViewHolder(final View itemView, final GithubUserAdapter adapter) {
        super(itemView);
        this.adapter = adapter;
        itemView.setOnClickListener(this);

        repoNameView = (TextView) itemView.findViewById(R.id.repo_name);
        starCountView = (TextView) itemView.findViewById(R.id.stars_count);
        descriptionView = (TextView) itemView.findViewById(R.id.description);
    }

    @Override
    public void onClick(final View v) {
        final Context context = v.getContext();
        final GithubRepo repo = adapter.getRepoAt(getLayoutPosition());
        if (repo == null) return;
        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(repo.getHtmlUrl())));
    }

    public void displayRepo(final GithubRepo repo) {
        repoNameView.setText(repo.getName());
        starCountView.setText(String.valueOf(repo.getStargazersCount()));
        descriptionView.setText(repo.getDescription());
        descriptionView.setVisibility(TextUtils.isEmpty(repo.getDescription()) ? View.GONE : View.VISIBLE);
    }
}
