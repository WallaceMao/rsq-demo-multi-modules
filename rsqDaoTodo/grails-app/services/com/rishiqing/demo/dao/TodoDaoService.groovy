package com.rishiqing.demo.dao

import com.rishiqing.demo.Todo
import grails.transaction.Transactional

@Transactional
class TodoDaoService {

    Todo saveOrUpdate(Todo todo) {
        todo.save(failOnError: true)
        todo
    }

    Todo remove(Todo todo){
        if(!todo.id){
            return null
        }
        todo.isDeleted = true
        todo.save(failOnError: true)
        todo
    }

    List getTodoList(){
        Todo.findAllByIsDeleted(false)
    }

    Todo getTodoById(long id){
        Todo.findById(id)
    }
}
