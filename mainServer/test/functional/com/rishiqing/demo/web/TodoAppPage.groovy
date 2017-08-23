package com.rishiqing.demo.web

import geb.Page
import org.openqa.selenium.Keys

/**
 * Created by  on 2017/8/21.Wallace
 */
class TodoAppPage extends Page {
    static url = "app#/all"
    static at = {
        title == "todo app"
        $('section.todoapp')
    }
    static content = {
        newInput(wait: 2, required: true) { $('input.new-todo') }
//        todoUl(wait: true, required: true) {$('ul.todo-list')}
        todoList() { $('ul.todo-list') }
    }

    def createTodo(String str){
        newInput.click()
        newInput << str
        newInput << Keys.ENTER
    }
}
