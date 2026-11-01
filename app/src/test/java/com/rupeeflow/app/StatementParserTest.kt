package com.rupeeflow.app

import com.rupeeflow.app.data.parser.CsvHelper
import com.rupeeflow.app.data.parser.GooglePayParser
import com.rupeeflow.app.data.parser.KotakParser
import com.rupeeflow.app.data.parser.PhonePeParser
import com.rupeeflow.app.domain.model.TransactionType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class StatementParserTest {

    private val syntheticKotakCsv = """
"","","Account Statement"
"ANONYMOUS ACCOUNT"
"","","","","Cust. Reln. No.","111222333"
"","","","","Account No.","999888777666"
"","","","","Period","From 01/09/2026 To 30/09/2026"
"","","","","Currency","INR"
"","","","","Branch","MAIN BRANCH"
"","","","","IFSC","KKBK0000001"

"Sl. No.","Transaction Date","Value Date","Description","Chq /Ref No.","Amount","Dr / Cr","Balance","Dr / Cr"
"1","01-09-2026 02:43:12","01-09-2026","UPI/TEST PAYEE/KKBK/214200208839/Payment from","UPI-624464458572","5,000.00","DR","22,000.65","CR"
"2","02-09-2026 12:27:16","02-09-2026","UPI/PRANAY GOPAL/ICIC/307630679490/Payment from","UPI-624485189063","800.00","DR","21,200.65","CR"
"3","04-09-2026 21:39:01","04-09-2026","UPI/TEST CREDITOR/HDFC/661315565088/UPI","UPI-624756766168","800.00","CR","22,000.65","CR"
"4","30-09-2026 07:47:53","30-09-2026","NEFT HDFCH01290623736 CORP SALARY HDFC0000240","NEFTINW-1731824539","33,067.00","CR","55,067.65","CR"
"5","01-10-2026 01:48:54","30-09-2026","Int.Pd:999888777666:01-07-2026 to 30-09-2026","","13.00","CR","55,080.65","CR"
"Closing Balance","as on 30/09/2026 INR 55,080.65"
    """.trimIndent()

    private val syntheticPhonePeCsv = """
Date,Merchant / UPI ID,Transaction Type,Amount (INR),Provider
2026-10-26,Zomato Limited (zomato@axis),DEBIT,"425.00",PhonePe
2026-10-27,MSEDCL Electricity Bill,DEBIT,"1,840.00",PhonePe
2026-10-28,Rohan Sharma (UPI Split),CREDIT,"650.00",PhonePe
    """.trimIndent()

    private val syntheticGPayCsv = """
Date,Description,Type,Amount,Source
2026-10-26,Blinkit Groceries UPI,DEBIT,"780.00",GPay
2026-10-27,Uber India Trip,DEBIT,"295.00",GPay
2026-10-28,Reward Scratch Card,CREDIT,"45.00",GPay
    """.trimIndent()

    @Test
    fun testKotakStatementParser() {
        val parser = KotakParser()
        val result = parser.parseText(syntheticKotakCsv)

        assertTrue("Kotak parser should produce transactions", result.items.isNotEmpty())
        assertEquals("Kotak Mahindra Bank", result.bankOrProviderName)
        assertEquals("55,080.65", result.detectedClosingBalance)

        // Verify UPI payee detection
        val payeeTx = result.items.firstOrNull { it.title.contains("TEST PAYEE", ignoreCase = true) }
        assertNotNull("Should detect TEST PAYEE", payeeTx)
        assertEquals(TransactionType.EXPENSE, payeeTx?.type)
        assertEquals(5000.0, payeeTx?.amount ?: 0.0, 0.01)

        // Verify NEFT salary credit
        val salaryTx = result.items.firstOrNull { it.rawDescription.contains("CORP SALARY", ignoreCase = true) }
        assertNotNull("Should find NEFT salary", salaryTx)
        assertEquals(TransactionType.EARNING, salaryTx?.type)
        assertEquals(33067.0, salaryTx?.amount ?: 0.0, 0.01)
        assertEquals("Salary / Income", salaryTx?.category)
    }

    @Test
    fun testPhonePeStatementParser() {
        val parser = PhonePeParser()
        val result = parser.parseText(syntheticPhonePeCsv)

        assertEquals("PhonePe", result.bankOrProviderName)
        assertEquals(3, result.items.size)

        val zomatoTx = result.items.first { it.rawDescription.contains("Zomato") }
        assertEquals(TransactionType.EXPENSE, zomatoTx.type)
        assertEquals(425.0, zomatoTx.amount, 0.01)
        assertEquals("Food & Dining", zomatoTx.category)

        val splitTx = result.items.first { it.rawDescription.contains("Rohan Sharma") }
        assertEquals(TransactionType.EARNING, splitTx.type)
        assertEquals(650.0, splitTx.amount, 0.01)
    }

    @Test
    fun testGooglePayStatementParser() {
        val parser = GooglePayParser()
        val result = parser.parseText(syntheticGPayCsv)

        assertEquals("Google Pay", result.bankOrProviderName)
        assertEquals(3, result.items.size)

        val blinkitTx = result.items.first { it.rawDescription.contains("Blinkit") }
        assertEquals(TransactionType.EXPENSE, blinkitTx.type)
        assertEquals(780.0, blinkitTx.amount, 0.01)
        assertEquals("Groceries", blinkitTx.category)

        val rewardTx = result.items.first { it.rawDescription.contains("Reward") }
        assertEquals(TransactionType.EARNING, rewardTx.type)
        assertEquals(45.0, rewardTx.amount, 0.01)
    }

    @Test
    fun testCsvHelperQuoting() {
        val line = """"1","01-09-2026","UPI/PRANAY","UPI-123","1,850.50","DR""""
        val cols = CsvHelper.parseCsvLine(line)

        assertEquals(6, cols.size)
        assertEquals("1,850.50", cols[4])
        assertEquals(1850.50, CsvHelper.cleanAmount(cols[4]), 0.01)
    }

    @Test
    fun testCanonicalPartyName() {
        val name1 = CsvHelper.canonicalPartyName("J PATIL PETROL")
        val name2 = CsvHelper.canonicalPartyName("J Patil Petrol")
        val name3 = CsvHelper.canonicalPartyName("NEFT • CORP SALARY")

        assertEquals("J PATIL PETROL", name1)
        assertEquals(name1, name2)
        assertEquals("CORP SALARY", name3)
    }
}
