package novoda.android.typewriter

import android.net.Uri__FromAndroid
import android.R
import com.xtremelabs.robolectric.bytecode.{RobolectricClassLoader, ShadowWrangler}
import com.xtremelabs.robolectric.internal.RealObject
import com.xtremelabs.robolectric.res.ResourceLoader
import com.xtremelabs.robolectric.shadows.ShadowApplication
import com.xtremelabs.robolectric.{ApplicationResolver, Robolectric, RobolectricConfig}
import java.io.File
import org.scalatest.{WordSpec, Suite, OneInstancePerTest, BeforeAndAfterEach}
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.mock.MockitoSugar

trait RoboSpecs extends BeforeAndAfterEach with OneInstancePerTest {
  this: Suite =>

  override def beforeEach() {
    setupApplicationState()
    super.beforeEach()
  }

  lazy val robolectricConfig = new RobolectricConfig(RoboSpecs.tmpManifestDir, RoboSpecs.tmpResDir, RoboSpecs.tmpAssetDir)
  lazy val resourceLoader = {
    val rClassName: String = robolectricConfig.getRClassName
    val rClass: Class[_] = Class.forName(rClassName)
    new ResourceLoader(robolectricConfig.getSdkVersion, rClass, robolectricConfig.getResourceDirectory, robolectricConfig.getAssetsDirectory)
  }

  def setupApplicationState() {
    robolectricConfig.validate()
    Robolectric.bindDefaultShadowClasses()
    Robolectric.resetStaticState()
    Robolectric.application = ShadowApplication.bind(new ApplicationResolver(robolectricConfig).resolveApplication, resourceLoader)
  }

  lazy val instrumentedClass = RoboSpecs.classLoader.bootstrap(this.getClass)

  override def newInstance = instrumentedClass.newInstance.asInstanceOf[Suite]
}

object R {
}

object RoboSpecs {

  lazy val tmpDir = {
    val f = new File(System.getProperty("java.io.tmpdir"))
    f.mkdirs()
    f
  }

  lazy val tmpResDir = {
    val f = new File(tmpDir, "res")
    f.mkdirs()
    f
  }

  lazy val tmpAssetDir = {
    val f = new File(tmpDir, "assets")
    f.mkdirs()
    f
  }

  lazy val tmpManifestDir = {
    val xml = <manifest xmlns:android="http://schemas.android.com/apk/res/android" package="novoda.android.typewriter">
      <application></application>
    </manifest>
    val f = new File(tmpDir, "AndroidManifest.xml")
    if (!f.exists()) {
      scala.xml.XML.save(f.getAbsolutePath, xml)
    }
    f
  }

  lazy val classHandler = ShadowWrangler.getInstance
  lazy val classLoader = {
    val loader = new RobolectricClassLoader(classHandler)
    loader.delegateLoadingOf("org.scalatest.")
    loader.delegateLoadingOf("org.mockito.")
    loader.delegateLoadingOf("scala.")

    List(classOf[Uri__FromAndroid],
      classOf[RoboSpecs],
      classOf[RobolectricClassLoader],
      classOf[RealObject],
      classOf[ShadowWrangler],
      classOf[RobolectricConfig],
      classOf[R]).foreach {
      classToDelegate => loader.delegateLoadingOf(classToDelegate.getName)
    }
    loader
  }
}

trait TypeWriterSpec extends WordSpec with ShouldMatchers with MockitoSugar with RoboSpecs