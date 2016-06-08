package catchla.yep.activity

import android.os.Bundle

import catchla.yep.R

/**
 * Created by mariotaku on 15/10/10.
 */
class AboutActivity : SwipeBackContentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment_content)
    }
}
