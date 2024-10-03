package com.example.recipebook.purchases

import android.app.Activity
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingFlowParams

import android.util.Log
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.Purchase

import com.android.billingclient.api.*
import com.example.recipebook.application.MyApplication
import com.google.android.material.dialog.MaterialAlertDialogBuilder

// A class to handle In-App Purchase processes in the app
class InAppPurchaseHandler(private val activity: Activity) {

    private lateinit var billingClient: BillingClient

    fun startConnection() {
        // Initialize the BillingClient
        billingClient = BillingClient.newBuilder(activity)
            .setListener { billingResult, purchases ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                    handlePurchase(purchases)
                }
                // Handle the result of the purchase

            }
            .enablePendingPurchases()
            .build()

        // Start the connection to Google Play Billing
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    // Billing service setup is complete
                    Log.d("InAppPurchase", "Billing setup finished")
                }
            }

            override fun onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to Google Play by calling startConnection() again.
                Log.d("InAppPurchase", "Billing service disconnected")
            }
        })
    }

    // Function to initiate the purchase flow
    private fun purchaseFlow(skuId: String) {
        val skuDetailsParams = SkuDetailsParams.newBuilder()
            .setSkusList(listOf(skuId))
            .setType(BillingClient.SkuType.SUBS)

        // Fetch SKU details for the selected product
        billingClient.querySkuDetailsAsync(skuDetailsParams.build()) { billingResult, skuDetailsList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && skuDetailsList != null) {
                // Assuming there's only one item in the list
                val skuDetail = skuDetailsList[0]

                // Prepare the billing flow parameters
                val billingFlowParams = BillingFlowParams.newBuilder()
                    .setSkuDetails(skuDetail)
                    .build()

                // Launch the billing flow (payment dialog)
                billingClient.launchBillingFlow(activity, billingFlowParams)
            } else {
                Log.d("InAppPurchase", "Error initiating purchase flow: ${billingResult.debugMessage}")
            }
        }
    }

    // Function to handle purchase result
    private fun handlePurchase(purchases: List<Purchase>?) {
            for (purchase in purchases!!) {
                if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED && !purchase.isAcknowledged) {
                    // Acknowledge the purchase
                    val ackPurchaseParams = AcknowledgePurchaseParams.newBuilder()
                        .setPurchaseToken(purchase.purchaseToken)
                        .build()

                    billingClient.acknowledgePurchase(ackPurchaseParams) { billingResult ->
                        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                            // Purchase acknowledged, set the variable to true in preferences
                            MyApplication.preference!!.setBoolean("PRODUCT_PURCHASED", true)

                            // Log for success
                            Log.d("InAppPurchase", "Purchase successful and acknowledged.")
                        }
                    }
                }
            }
    }

    // Function to show the Material Alert Dialog for purchase confirmation
    fun showConfirmationDialog(skuDetail: String) {
        // Creating a Material AlertDialog
        MaterialAlertDialogBuilder(activity)
            .setTitle("Confirm Purchase")
            .setMessage("Do you want to purchase this item?")
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            .setPositiveButton("Buy") { dialog, _ ->
                purchaseFlow(skuDetail) // Proceed to purchase
                dialog.dismiss() // Close the dialog
            }
            .show()
    }
}

