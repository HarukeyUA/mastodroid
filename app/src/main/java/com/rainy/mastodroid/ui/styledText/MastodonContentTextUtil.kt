package com.rainy.mastodroid.ui.styledText

import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.nodes.Node
import org.jsoup.nodes.TextNode
import org.jsoup.select.NodeVisitor

enum class MastodonContentTag {
    HASHTAG, MENTION, URL
}

private val emojiRegex = "(?<=:)(.*?)(?=:)".toRegex() // Everything between ':' and ':' non inclusive

fun String.annotateMastodonContent(emojiShortCodes: List<String> = listOf()): AnnotatedString {
    return buildAnnotatedString {
        traverseContent(this@annotateMastodonContent, this, emojiShortCodes)
    }
}

fun String.annotateMastodonEmojis(emojiShortCodes: List<String> = listOf()): AnnotatedString {
    return buildAnnotatedString {
        annotateInlineEmojis(this@annotateMastodonEmojis, emojiShortCodes, this)
    }
}

private fun traverseContent(
    string: String,
    to: AnnotatedString.Builder,
    emojiShortCodes: List<String>
) {
    val htmlDocument = Jsoup.parse(string)
    val invisible = htmlDocument.getElementsByClass("invisible")
    invisible.remove()
    htmlDocument.traverse(object : NodeVisitor {
        override fun head(node: Node, depth: Int) {
            if (node is TextNode) {
                annotateInlineEmojis(node.text(), emojiShortCodes, to)
            } else if (node is Element) {
                when (node.nodeName()) {
                    "a" -> {
                        val link = node.attr("href")
                        if (!link.isNullOrEmpty()) {
                            when {
                                node.hasClass("hashtag") -> {
                                    to.pushStringAnnotation(
                                        MastodonContentTag.HASHTAG.toString(),
                                        link
                                    )
                                }

                                node.hasClass("mention") -> {
                                    to.pushStringAnnotation(
                                        MastodonContentTag.MENTION.toString(),
                                        link
                                    )
                                }

                                else -> {
                                    to.pushStringAnnotation(MastodonContentTag.URL.toString(), link)
                                }
                            }
                        }
                    }

                    "br" -> {
                        to.append('\n')
                    }
                }
            }
        }

        override fun tail(node: Node, depth: Int) {
            if (node.nodeName().toString() == "a" && !node.attr("href").isNullOrEmpty()) {
                to.pop()
            }
            if (node.nodeName() == "p" && node.nextSibling() != null) {
                to.append('\n')
            }
        }
    })
}

private fun annotateInlineEmojis(
    text: String,
    shortcodes: List<String>,
    to: AnnotatedString.Builder
) {
    val emojiPositions = emojiRegex.findAll(text)
        .filter { shortcodes.contains(it.value) }
    text.forEachIndexed { index: Int, c: Char ->
        val emojiPosition =
            emojiPositions.find { it.range.any { rangeElement -> rangeElement in index - 1..index + 1 } } // Account for custom emoji ':' parenthesis
        if (emojiPosition?.range?.first == index) {
            emojiPosition.also {
                to.appendInlineContent(emojiPosition.value)
            }
        }

        if (emojiPosition == null) {
            to.append(c)
        }
    }
}
