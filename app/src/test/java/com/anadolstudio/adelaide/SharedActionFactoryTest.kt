package com.anadolstudio.adelaide

import com.anadolstudio.core.share_util.SharedActionFactory
import org.junit.Assert.assertEquals
import org.junit.Test

class SharedActionFactoryTest {

    @Test
    fun packageTest() {
        SharedActionFactory.instance().forEach { action ->
            val packageName = when (action) {
                is SharedActionFactory.Empty -> null
                is SharedActionFactory.VK -> "com.vkontakte.android"
                is SharedActionFactory.Instagram -> "com.instagram.android"
                is SharedActionFactory.Facebook -> "com.facebook.katana"
                is SharedActionFactory.Messenger -> "com.facebook.orca"
                is SharedActionFactory.WhatsApp -> "com.whatsapp"
                is SharedActionFactory.Twitter -> "com.twitter.android"
            }

            assertEquals(packageName, action.appPackage?.appPackage)
        }
    }

}
