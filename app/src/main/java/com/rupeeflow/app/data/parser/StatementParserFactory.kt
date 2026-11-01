package com.rupeeflow.app.data.parser

object StatementParserFactory {

    private val allParsers: List<StatementParser> = listOf(
        KotakParser(),
        PhonePeParser(),
        GooglePayParser(),
        HdfcParser(),
        SbiParser(),
        IciciParser(),
        GenericUpiParser()
    )

    fun getParser(providerHint: String? = null, headerLines: List<String> = emptyList()): StatementParser {
        if (!providerHint.isNullOrBlank() && !providerHint.equals("AUTO", ignoreCase = true)) {
            val matched = allParsers.firstOrNull { 
                it.providerName.contains(providerHint, ignoreCase = true) || 
                providerHint.contains(it.providerName, ignoreCase = true) 
            }
            if (matched != null) return matched
        }

        if (headerLines.isNotEmpty()) {
            for (parser in allParsers.dropLast(1)) { // Exclude GenericUpiParser from first pass
                if (parser.canParse(headerLines)) {
                    return parser
                }
            }
        }

        return GenericUpiParser()
    }
}
