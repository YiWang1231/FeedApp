package com.peter.feedapp.bean

data class Banner(var title: String,
             var imagePath: String,
             var url: String) {
    constructor():this("","","")
}