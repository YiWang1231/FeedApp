package com.peter.feedapp.bean
import com.google.gson.annotations.Expose


data class Course(var id: Int?,
                  var title: String?,
                  @Expose(serialize = true, deserialize = false) var desc: String?,
                  var envelopePic: String?,
                  @Expose(serialize = true, deserialize = false) var author: String?,
                  var niceDate: String?,
                  var link: String?,
                  var superChapterName: String?,
                  var chapterName: String?,
                  @Expose(serialize = true, deserialize = false) var tag: String?,
                  var fresh: Boolean?,
                  var type: Int?)