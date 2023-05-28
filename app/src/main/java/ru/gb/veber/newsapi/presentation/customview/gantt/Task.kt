package ru.gb.veber.newsapi.presentation.customview.gantt

import java.time.LocalDate

data class Task(
    val name: String,
    val dateStart: LocalDate,
    val dateEnd: LocalDate,
)