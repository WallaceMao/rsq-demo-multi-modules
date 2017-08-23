package com.rishiqing.demo

import grails.transaction.Transactional

@Transactional
class TodoService {
    def todoDaoService

    List getTodoMapList() {
        todoDaoService.getTodoList()*.toMap()
    }

    Map saveOrUpdateTodo(Map params){
        Todo todo
        if(params.id){
            todo = todoDaoService.getTodoById(params.id)
            if(!todo){
                return new RuntimeException("todo with id ${params.id} not found")
            }
            todo.properties = params
        }else{
            todo = new Todo(params)
        }
        todo = todoDaoService.saveOrUpdate(todo)
        todo.toMap()
    }

    Map removeTodo(Map params){
        Todo todo = todoDaoService.getTodoById(params.id)
        todo = todoDaoService.remove(todo)
        todo.toMap()
    }
}
