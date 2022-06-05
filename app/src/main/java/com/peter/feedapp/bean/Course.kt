package com.peter.feedapp.bean

data class Course(var id: Int?,
                  var title: String?,
                  var desc: String?,
                  var envelopePic: String?,
                  var author: String?,
                  var shareUser: String?,
                  var niceDate: String?,
                  var link: String?,
                  var superChapterName: String?,
                  var chapterName: String?,
                  var tags: List<Tag>?,
                  var showTag: String?,
                  var fresh: Boolean?,
                  var type: Int?)