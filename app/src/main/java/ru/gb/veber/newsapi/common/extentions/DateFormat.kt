package ru.gb.veber.newsapi.common.extentions

//TODO Поменять логику сохранения
fun String.checkLogin(): String {
    return if (this.length >= 7) {
        substring(0, 7)
    } else {
        this
    }
}
