package com.example.data.payment

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import com.example.core.util.MoneyParser
import com.example.domain.model.BankApp
import com.example.domain.model.PaymentInfo

interface BankAppLauncher {
    fun getInstalledBanks(): List<BankApp>
    fun getAllSupportedBanks(): List<BankApp>
    fun openBank(bank: BankApp, paymentInfo: PaymentInfo): Boolean
    fun copyPaymentDetails(paymentInfo: PaymentInfo): String
}

class AndroidBankAppLauncher(
    private val context: Context
) : BankAppLauncher {

    private val supportedBanks = listOf(
        BankApp(
            id = "MB",
            displayName = "MB Bank",
            shortName = "MB",
            packageName = "com.mbmobile",
            accentColorHex = 0xFF004AC6,
            deepLinkPrefix = "mbmobile://"
        ),
        BankApp(
            id = "VCB",
            displayName = "Vietcombank",
            shortName = "VCB",
            packageName = "com.VCB",
            accentColorHex = 0xFF005A3C,
            deepLinkPrefix = "vietcombank://"
        ),
        BankApp(
            id = "TCB",
            displayName = "Techcombank",
            shortName = "TCB",
            packageName = "vn.com.techcombank.bb.app",
            accentColorHex = 0xFFD81921,
            deepLinkPrefix = "techcombank://"
        ),
        BankApp(
            id = "VPB",
            displayName = "VPBank",
            shortName = "VPB",
            packageName = "com.vnpay.vpbankonline",
            accentColorHex = 0xFF009A49,
            deepLinkPrefix = "vpbank://"
        ),
        BankApp(
            id = "MOMO",
            displayName = "MoMo",
            shortName = "MoMo",
            packageName = "com.mservice.momotransfer",
            accentColorHex = 0xFFAE2070,
            deepLinkPrefix = "momo://"
        ),
        BankApp(
            id = "BIDV",
            displayName = "BIDV SmartBanking",
            shortName = "BIDV",
            packageName = "com.vnpay.bidv",
            accentColorHex = 0xFF004D40,
            deepLinkPrefix = "bidvsmartbanking://"
        ),
        BankApp(
            id = "CTG",
            displayName = "VietinBank iPay",
            shortName = "iPay",
            packageName = "com.vietinbank.ipay",
            accentColorHex = 0xFF006699,
            deepLinkPrefix = "vietinbankipay://"
        ),
        BankApp(
            id = "TPB",
            displayName = "TPBank Mobile",
            shortName = "TPB",
            packageName = "com.tpb.mb.gprsandroid",
            accentColorHex = 0xFF6B21A8,
            deepLinkPrefix = "tpbank://"
        )
    )

    override fun getAllSupportedBanks(): List<BankApp> {
        val pm = context.packageManager
        return supportedBanks.map { bank ->
            val isInstalled = try {
                pm.getPackageInfo(bank.packageName, 0)
                true
            } catch (e: PackageManager.NameNotFoundException) {
                // In demo / simulator mode on device or desktop
                false
            }
            bank.copy(isInstalled = isInstalled)
        }
    }

    override fun getInstalledBanks(): List<BankApp> {
        val all = getAllSupportedBanks()
        val installed = all.filter { it.isInstalled }
        // If on emulator/no bank apps installed, return top banks marked as available for smooth UX
        return if (installed.isNotEmpty()) {
            installed
        } else {
            // Provide popular banks for testing
            all.take(5).map { it.copy(isInstalled = true) }
        }
    }

    override fun copyPaymentDetails(paymentInfo: PaymentInfo): String {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        val textToCopy = buildString {
            paymentInfo.accountNumber?.let { append("STK: $it\n") }
            paymentInfo.amount?.let { append("Số tiền: ${MoneyParser.formatCurrency(it)}\n") }
            paymentInfo.content?.let { append("Nội dung: $it\n") }
            paymentInfo.receiverName?.let { append("Người nhận: $it") }
        }
        val clip = ClipData.newPlainText("Thông tin thanh toán VietQR", textToCopy)
        clipboard?.setPrimaryClip(clip)
        return textToCopy
    }

    override fun openBank(bank: BankApp, paymentInfo: PaymentInfo): Boolean {
        // First copy payload to clipboard
        copyPaymentDetails(paymentInfo)

        val pm = context.packageManager
        val launchIntent = pm.getLaunchIntentForPackage(bank.packageName)
        if (launchIntent != null) {
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(launchIntent)
            return true
        }

        // Deep link or web fallback
        if (!bank.deepLinkPrefix.isNullOrEmpty()) {
            try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(bank.deepLinkPrefix)).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
                return true
            } catch (e: Exception) {
                // Ignore fallback failure
            }
        }

        // Fallback: Open Google Play Store or web
        try {
            val storeIntent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=${bank.packageName}")).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(storeIntent)
            return true
        } catch (e: Exception) {
            return false
        }
    }
}
