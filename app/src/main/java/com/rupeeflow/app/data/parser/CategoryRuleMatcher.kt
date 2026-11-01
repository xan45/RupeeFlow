package com.rupeeflow.app.data.parser

import com.rupeeflow.app.domain.model.TransactionType

object CategoryRuleMatcher {

    fun match(description: String, type: TransactionType): String {
        val lower = description.lowercase().trim()

        if (type == TransactionType.EARNING) {
            return when {
                lower.contains("wipro") || lower.contains("infosys") || lower.contains("tcs") ||
                lower.contains("salary") || lower.contains("neft") || lower.contains("invoice") ||
                lower.contains("int.pd") || lower.contains("interest") || lower.contains("dividend") ||
                lower.contains("stipend") -> "Salary / Income"
                lower.contains("cashback") || lower.contains("reward") || lower.contains("scratch") -> "Other"
                else -> "Other"
            }
        }

        // Expense categories
        return when {
            containsAny(lower, 
                "swiggy", "zomato", "starbucks", "chaha", "dosa", "pet puja", 
                "pan sho", "cafe", "restaurant", "mcdonald", "kfc", "domino", 
                "pizza", "burger", "chai", "tea", "bakery", "dhaba", "food", "kitchen"
            ) -> "Food & Dining"

            containsAny(lower, 
                "blinkit", "instamart", "zepto", "super sh", "super m", "grocery", 
                "dmart", "bigbasket", "nature basket", "reliance fresh", "mart", 
                "provision", "store", "vegetable", "fruits", "dairy", "milk"
            ) -> "Groceries"

            containsAny(lower, 
                "uber", "ola", "irctc", "petrol", "fuel", "indian oil", "bharat petroleum", 
                "hpcl", "shell", "auto", "taxi", "metro", "fastag", "toll", "indigo", 
                "air india", "railway", "bus", "transport"
            ) -> "Travel"

            containsAny(lower, 
                "vodafone", "idea", "jio", "airtel", "electricity", "msedcl", "recharge", 
                "broadband", "tataplay", "dth", "water", "gas", "adani", "bescom", 
                "billdesk", "bbps", "postpaid", "prepaid", "fiber"
            ) -> "Bills & UPI"

            containsAny(lower, 
                "amazon", "flipkart", "myntra", "ajio", "meesho", "nykaa", "zara", 
                "decathlon", "tata cliq", "lenskart", "retail", "croma", "reliancedigital"
            ) -> "Shopping"

            containsAny(lower, 
                "zerodha", "groww", "coin", "angelone", "upstox", "mutual fund", "sip", 
                "cams", "kfintech", "share", "navi", "kuvera", "sebi", "stock"
            ) -> "Investments"

            containsAny(lower, 
                "pharmacy", "apollo", "1mg", "pharmeasy", "medplus", "hospital", 
                "clinic", "diagnostic", "doctor", "lab", "health", "dental"
            ) -> "Health"

            containsAny(lower, 
                "bookmyshow", "netflix", "prime video", "hotstar", "spotify", 
                "youtube", "pvr", "inox", "sonyliv", "cinema", "movie", "entertainment"
            ) -> "Entertainment"

            else -> "Other"
        }
    }

    private fun containsAny(text: String, vararg keywords: String): Boolean {
        return keywords.any { text.contains(it) }
    }
}
