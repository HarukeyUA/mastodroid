package com.rainy.mastodroid.ui.styledText

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

fun String.annotateMastodonContent(): AnnotatedString {
    return buildAnnotatedString {
        traverseContent(this@annotateMastodonContent, this)
    }
}

private fun traverseContent(string: String, to: AnnotatedString.Builder) {
    val htmlDocument = Jsoup.parse(string)
    val invisible = htmlDocument.getElementsByClass("invisible")
    invisible.remove()
    htmlDocument.traverse(object : NodeVisitor {
        override fun head(node: Node, depth: Int) {
            if (node is TextNode) {
                to.append(node.text())
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
