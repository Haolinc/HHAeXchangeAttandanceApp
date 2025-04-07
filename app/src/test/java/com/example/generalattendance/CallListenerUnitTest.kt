package com.example.generalattendance

import android.content.Context
import android.os.Build
import android.telephony.TelephonyManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowTelephonyManager

@RunWith(RobolectricTestRunner::class)
class CallListenerUnitTest {
    private lateinit var context: Context
    private lateinit var telephonyManager: TelephonyManager
    private lateinit var callListener: CallListener
    private lateinit var shadowTelephonyManager: ShadowTelephonyManager

    @Mock
    private lateinit var mockOnIdle: () -> Unit
    @Mock
    private lateinit var mockOnOffHook: () -> Unit
    @Mock
    private lateinit var mockOnRinging: () -> Unit

    @Before
    fun setUp() {
        context = RuntimeEnvironment.getApplication()
        telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        shadowTelephonyManager = shadowOf(telephonyManager)
        MockitoAnnotations.openMocks(this)

        callListener = CallListener(
            context = context,
            onCallStateIdleFunction = mockOnIdle,
            onCallStateOffHookFunction = mockOnOffHook,
            onCallStateRingingFunction = mockOnRinging
        )
    }

    // API Version Tests
    @Test
    @Config(sdk = [Build.VERSION_CODES.S])
    fun whenCallListenerRegisteredAtVersionS_telephonyCallBackShouldNOTBeNullANDPhoneStateListenerShouldBeNull(){
        callListener.register()

        assertNotNull(callListener.telephonyCallback)
        assertEquals(callListener.phoneStateListener, null)
    }
    @Test
    @Config(sdk = [Build.VERSION_CODES.R])
    fun whenCallListenerRegisteredAtVersionR_telephonyCallBackShouldBeNullANDPhoneStateListenerShouldNOTBeNull(){
        callListener.register()

        assertNotNull(callListener.phoneStateListener)
        assertEquals(callListener.telephonyCallback, null)
    }


    // Call State onIdle Tests - Version S
    @Test
    @Config(sdk = [Build.VERSION_CODES.S])
    fun whenCallListenerRegisteredAtVersionS_mockOnIdleShouldBeInvoked(){
        callListener.register()

        // The listener start with idle

        verify(mockOnIdle).invoke()
    }

    @Test
    @Config(sdk = [Build.VERSION_CODES.S])
    fun whenCallListenerRegisteredANDSetCallStateToIdleAtVersionS_mockOnIdleShouldBeInvokedTwice(){
        callListener.register()

        // The listener start with idle
        shadowTelephonyManager.setCallState(TelephonyManager.CALL_STATE_IDLE)

        verify(mockOnIdle, times(2)).invoke()
    }

    @Test
    @Config(sdk = [Build.VERSION_CODES.S])
    fun whenCallListenerRegisteredAtVersionS_mockOnOffHookANDmockOnRingingShouldNOTBeInvoked(){
        callListener.register()

        // The listener start with idle

        verifyNoInteractions(mockOnOffHook)
        verifyNoInteractions(mockOnRinging)
    }

    @Test
    @Config(sdk = [Build.VERSION_CODES.S])
    fun whenCallListenerRegisteredANDSetCallStateToIdleAtVersionS_mockOnOffHookANDmockOnRingingShouldNOTBeInvoked(){
        callListener.register()

        // The listener start with idle
        shadowTelephonyManager.setCallState(TelephonyManager.CALL_STATE_IDLE)

        verifyNoInteractions(mockOnOffHook)
        verifyNoInteractions(mockOnRinging)
    }

    // Call State onOffHook Tests - Version S
    @Test
    @Config(sdk = [Build.VERSION_CODES.S])
    fun whenCallListenerRegisteredANDSetCallStateToOffHookAtVersionS_mockOnIdleANDmockOnOffHookShouldBeInvoked(){
        callListener.register()

        // The listener start with idle

        shadowTelephonyManager.setCallState(TelephonyManager.CALL_STATE_OFFHOOK)

        verify(mockOnIdle).invoke()
        verify(mockOnOffHook).invoke()
    }

    @Test
    @Config(sdk = [Build.VERSION_CODES.S])
    fun whenCallListenerRegisteredANDSetCallStateToOffHookAtVersionS_mockOnRingingShouldNOTBeInvoked(){
        callListener.register()

        // The listener start with idle

        verifyNoInteractions(mockOnRinging)
    }

    // Call State onRinging Tests - Version S
    @Test
    @Config(sdk = [Build.VERSION_CODES.S])
    fun whenCallListenerRegisteredANDSetCallStateToRingingAtVersionS_mockOnIdleANDmockOnRingingShouldBeInvoked(){
        callListener.register()

        // The listener start with idle

        shadowTelephonyManager.setCallState(TelephonyManager.CALL_STATE_RINGING)

        verify(mockOnIdle).invoke()
        verify(mockOnRinging).invoke()
    }

    @Test
    @Config(sdk = [Build.VERSION_CODES.S])
    fun whenCallListenerRegisteredANDSetCallStateToRingingAtVersionS_mockOnOffHookShouldNOTBeInvoked(){
        callListener.register()

        // The listener start with idle

        verifyNoInteractions(mockOnOffHook)
    }

    // Call Listener Unregister Tests - Version S
    @Test
    @Config(sdk = [Build.VERSION_CODES.S])
    fun whenCallListenerUnregisteredANDSetCallStateToIdleAtVersionS_mockOnIdleShouldInvokedOnceANDmockOnOffHookANDmockOnRingingShouldNOTBeInvoked(){
        callListener.register()

        // The listener start with idle

        callListener.unregister()
        shadowTelephonyManager.setCallState(TelephonyManager.CALL_STATE_IDLE)

        verify(mockOnIdle).invoke()
        verifyNoInteractions(mockOnOffHook)
        verifyNoInteractions(mockOnRinging)
    }

    @Test
    @Config(sdk = [Build.VERSION_CODES.S])
    fun whenCallListenerUnregisteredANDSetCallStateToOffHookAtVersionS_mockOnIdleShouldInvokedOnceANDmockOnOffHookANDmockOnRingingShouldNOTBeInvoked(){
        callListener.register()

        // The listener start with idle

        callListener.unregister()
        shadowTelephonyManager.setCallState(TelephonyManager.CALL_STATE_OFFHOOK)

        verify(mockOnIdle).invoke()
        verifyNoInteractions(mockOnOffHook)
        verifyNoInteractions(mockOnRinging)
    }

    @Test
    @Config(sdk = [Build.VERSION_CODES.S])
    fun whenCallListenerUnregisteredANDSetCallStateToRingingAtVersionS_mockOnIdleShouldInvokedOnceANDmockOnOffHookANDmockOnRingingShouldNOTBeInvoked(){
        callListener.register()

        // The listener start with idle

        callListener.unregister()
        shadowTelephonyManager.setCallState(TelephonyManager.CALL_STATE_RINGING)

        verify(mockOnIdle).invoke()
        verifyNoInteractions(mockOnOffHook)
        verifyNoInteractions(mockOnRinging)
    }

    // Call State onIdle Tests - Version R
    @Test
    @Config(sdk = [Build.VERSION_CODES.R])
    fun whenCallListenerRegisteredAtVersionR_mockOnIdleShouldBeInvoked(){
        callListener.register()

        // The listener start with idle

        verify(mockOnIdle).invoke()
    }

    @Test
    @Config(sdk = [Build.VERSION_CODES.R])
    fun whenCallListenerRegisteredANDSetCallStateToIdleAtVersionR_mockOnIdleShouldBeInvokedTwice(){
        callListener.register()

        // The listener start with idle
        shadowTelephonyManager.setCallState(TelephonyManager.CALL_STATE_IDLE)

        verify(mockOnIdle, times(2)).invoke()
    }

    @Test
    @Config(sdk = [Build.VERSION_CODES.R])
    fun whenCallListenerRegisteredAtVersionR_mockOnOffHookANDmockOnRingingShouldNOTBeInvoked(){
        callListener.register()

        // The listener start with idle

        verifyNoInteractions(mockOnOffHook)
        verifyNoInteractions(mockOnRinging)
    }

    @Test
    @Config(sdk = [Build.VERSION_CODES.R])
    fun whenCallListenerRegisteredANDSetCallStateToIdleAtVersionR_mockOnOffHookANDmockOnRingingShouldNOTBeInvoked(){
        callListener.register()

        // The listener start with idle
        shadowTelephonyManager.setCallState(TelephonyManager.CALL_STATE_IDLE)

        verifyNoInteractions(mockOnOffHook)
        verifyNoInteractions(mockOnRinging)
    }

    // Call State onOffHook Tests - Version R
    @Test
    @Config(sdk = [Build.VERSION_CODES.R])
    fun whenCallListenerRegisteredANDSetCallStateToOffHookAtVersionR_mockOnIdleANDmockOnOffHookShouldBeInvoked(){
        callListener.register()

        // The listener start with idle

        shadowTelephonyManager.setCallState(TelephonyManager.CALL_STATE_OFFHOOK)

        verify(mockOnIdle).invoke()
        verify(mockOnOffHook).invoke()
    }

    @Test
    @Config(sdk = [Build.VERSION_CODES.R])
    fun whenCallListenerRegisteredANDSetCallStateToOffHookAtVersionR_mockOnRingingShouldNOTBeInvoked(){
        callListener.register()

        // The listener start with idle

        verifyNoInteractions(mockOnRinging)
    }

    // Call State onRinging Tests - Version R
    @Test
    @Config(sdk = [Build.VERSION_CODES.R])
    fun whenCallListenerRegisteredANDSetCallStateToRingingAtVersionR_mockOnIdleANDmockOnRingingShouldBeInvoked(){
        callListener.register()

        // The listener start with idle

        shadowTelephonyManager.setCallState(TelephonyManager.CALL_STATE_RINGING)

        verify(mockOnIdle).invoke()
        verify(mockOnRinging).invoke()
    }

    @Test
    @Config(sdk = [Build.VERSION_CODES.R])
    fun whenCallListenerRegisteredANDSetCallStateToRingingAtVersionR_mockOnOffHookShouldNOTBeInvoked(){
        callListener.register()

        // The listener start with idle

        verifyNoInteractions(mockOnOffHook)
    }

    // Call Listener Unregister Tests - Version R
    @Test
    @Config(sdk = [Build.VERSION_CODES.R])
    fun whenCallListenerUnregisteredANDSetCallStateToIdleAtVersionR_mockOnIdleShouldInvokedOnceANDmockOnOffHookANDmockOnRingingShouldNOTBeInvoked(){
        callListener.register()

        // The listener start with idle

        callListener.unregister()
        shadowTelephonyManager.setCallState(TelephonyManager.CALL_STATE_IDLE)

        verify(mockOnIdle).invoke()
        verifyNoInteractions(mockOnOffHook)
        verifyNoInteractions(mockOnRinging)
    }

    @Test
    @Config(sdk = [Build.VERSION_CODES.R])
    fun whenCallListenerUnregisteredANDSetCallStateToOffHookAtVersionR_mockOnIdleShouldInvokedOnceANDmockOnOffHookANDmockOnRingingShouldNOTBeInvoked(){
        callListener.register()

        // The listener start with idle

        callListener.unregister()
        shadowTelephonyManager.setCallState(TelephonyManager.CALL_STATE_OFFHOOK)

        verify(mockOnIdle).invoke()
        verifyNoInteractions(mockOnOffHook)
        verifyNoInteractions(mockOnRinging)
    }

    @Test
    @Config(sdk = [Build.VERSION_CODES.R])
    fun whenCallListenerUnregisteredANDSetCallStateToRingingAtVersionR_mockOnIdleShouldInvokedOnceANDmockOnOffHookANDmockOnRingingShouldNOTBeInvoked(){
        callListener.register()

        // The listener start with idle

        callListener.unregister()
        shadowTelephonyManager.setCallState(TelephonyManager.CALL_STATE_RINGING)

        verify(mockOnIdle).invoke()
        verifyNoInteractions(mockOnOffHook)
        verifyNoInteractions(mockOnRinging)
    }
}