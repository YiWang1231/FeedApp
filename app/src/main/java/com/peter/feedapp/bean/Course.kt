package com.peter.feedapp.bean


data class Course(var id: Int,
             var title: String,
             var desc: String,
             var envelopePic: String,
             var author: String,
             var niceDate: String,
             var link: String,
             var superChapterName: String,
             var chapterName: String,
             var tag: String,
             var fresh: Boolean,
             var type: Int) {

    constructor():this(0, "", "", "", "", "", "", "", "",  "", false, 0) {

    }
}