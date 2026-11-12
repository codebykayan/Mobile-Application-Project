package com.example.tinystep_stage2

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

object AppRepository {
    var children by mutableStateOf(
        listOf(
            ChildProfile(id = "1", name = "Sara", age = 8, avatarColor = 0xFFFFE3A3),
            ChildProfile(id = "2", name = "Omar", age = 10, avatarColor = 0xFFCDE4FA),
        )
    )
        private set

    fun addChild(name: String, age: Int): ChildProfile {
        val newId = (children.size + 1).toString()
        val avatarColors = listOf(0xFFFFE3A3, 0xFFCDE4FA, 0xFFFFD180, 0xFFE1BEE7, 0xFFC8E6C9)
        val color = avatarColors[children.size % avatarColors.size]
        val newChild = ChildProfile(id = newId, name = name, age = age, avatarColor = color)
        children = children + newChild
        return newChild
    }

    fun updateChild(id: String, name: String, age: Int) {
        children = children.map {
            if (it.id == id) it.copy(name = name, age = age) else it
        }
    }

    fun deleteChild(id: String) {
        children = children.filterNot { it.id == id }
    }

    fun getChildById(id: String): ChildProfile? {
        return children.find { it.id == id }
    }

    fun updateChildrenList(newChildren: List<ChildProfile>) {
        children = newChildren
    }
}
