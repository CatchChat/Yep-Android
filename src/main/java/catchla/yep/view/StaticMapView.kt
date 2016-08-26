package catchla.yep.view

import android.content.Context
import android.location.Location
import android.util.AttributeSet
import android.widget.ImageView
import catchla.yep.util.ImageLoaderWrapper
import catchla.yep.util.StaticMapUrlGenerator
import catchla.yep.util.dagger.GeneralComponentHelper
import javax.inject.Inject

/**
 * Created by mariotaku on 15/12/9.
 */
class StaticMapView : ImageView {
    internal var generator = StaticMapUrlGenerator()
    @Inject
    lateinit var imageLoader: ImageLoaderWrapper
    private var location: Location? = null
    private var zoomLevel: Int = 0
    private val scaleToDensity: Boolean = false

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        if (!isInEditMode) {
            GeneralComponentHelper.build(context).inject(this)
        }
        scaleType = ImageView.ScaleType.CENTER_CROP
    }

    fun setProvider(provider: StaticMapUrlGenerator.Provider) {
        generator.setProvider(provider)
    }

    fun setScaleToDensity(scaleToDensity: Boolean) {
        generator.setScale(if (scaleToDensity) resources.displayMetrics.density else 1f)
        loadImage()
    }

    fun display(location: Location, zoomLevel: Int) {
        this.location = location
        this.zoomLevel = zoomLevel
        if (!generator.hasSize()) {
            generator.setSize(width, height)
        }
        loadImage()
    }

    private fun loadImage() {
        if (location == null || !generator.hasSize() || isInEditMode) return
        imageLoader.displayImage(generator.generate(location, zoomLevel), this)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (w != oldw || h != oldh) {
            generator.setSize(w, h)
            loadImage()
        }
    }
}
