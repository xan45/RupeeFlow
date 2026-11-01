package com.rupeeflow.app.data.parser

import java.io.InputStream

interface StatementParser {
    val providerName: String
    fun canParse(headerLines: List<String>): Boolean
    fun parse(inputStream: InputStream): ParsedStatementResult
    fun parseText(rawText: String): ParsedStatementResult
}
