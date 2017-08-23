package com.rishiqing.demo

import grails.test.spock.IntegrationSpec
import org.hibernate.SessionFactory
import spock.lang.Ignore
import spock.lang.Shared

class TodoServiceIntegrationSpec extends IntegrationSpec {
    static transactional = true

    SessionFactory sessionFactory

    @Shared def todoDaoService
    @Shared def todoService

    @Shared Todo todo1
    @Shared Todo todo2
    @Shared Todo todo3
    @Shared Todo todo4
    @Shared Todo todo5
    @Shared Todo todo6

    def setupSpec(){
    }

    def cleanupSpec(){}

    def setup() {
        Date now = new Date()
        def (strTitle1, strTitle2, strTitle3, strTitle4, strTitle5, strTitle6) =
        ['aaa','bbb','ccc','ddd', 'eee', 'fff']

        todo1 = new Todo(title: strTitle1)
        todo1 = todoDaoService.saveOrUpdate(todo1)
        todo2 = new Todo(title: strTitle2, isDone: true, doneTime: now.getTime())
        todoDaoService.saveOrUpdate(todo2)
        todo3 = new Todo(title: strTitle3, isDeleted: true, deletedTime: now.getTime())
        todoDaoService.saveOrUpdate(todo3)
        todo4 = new Todo(title: strTitle4, isDone: true, doneTime: now.getTime(), isDeleted: true, deletedTime: now.getTime())
        todoDaoService.saveOrUpdate(todo4)
        todo5 = new Todo(title: strTitle5)
        todoDaoService.saveOrUpdate(todo5)
        todo6 = new Todo(title: strTitle6)
        todoDaoService.saveOrUpdate(todo6)

    }

    def cleanup() {}

    void "todoService.getTodoMapList get result map"() {
        when:
        List list = todoService.getTodoMapList()

        then:
        list.size() == 4  //isDeleted不会被读取
        todo1.toMap() == list[0]
        todo2.toMap() == list[1]
        todo5.toMap() == list[2]
        todo6.toMap() == list[3]
    }

    void "todoService.saveOrUpdateTodo should successfully save"(){
        when:
        String title = "new saved todo ${new Date()}"
        Map result = todoService.saveOrUpdateTodo([title: title])

        then:
        result.id != null
        result.title == title

        when:
        Todo todo = todoDaoService.getTodoById(result.id)

        then:
        todo.id == result.id
        todo.title == result.title
    }

    void "todoService.saveOrUpdateTodo should successfully update title"(){
        when:
        String titleChanged = "titleChanged${new Date()}"
        Map result = todoService.saveOrUpdateTodo([id: todo1.id, title: titleChanged])
        Todo todo = todoDaoService.getTodoById(todo1.id)

        then:
        result.id == todo1.id
        result.title == titleChanged
        todo.id == todo1.id
        todo.title == titleChanged
    }
    void "todoService.saveOrUpdateTodo should successfully update isDone"(){
        when:
        Date now = new Date()
        Map result = todoService.saveOrUpdateTodo([id: todo1.id, isDone: true, doneTime: now.getTime()])
        Todo todo = todoDaoService.getTodoById(todo1.id)

        then:
        result.id == todo1.id
        result.isDone == true
        result.doneTime == now.getTime()
        todo.id == todo1.id
        todo.isDone == true
        todo.doneTime == now.getTime()
    }

    void "todoService.removeTodo should successfully remove"(){
        when:
        Map result = todoService.removeTodo([id: todo5.id])
        Todo deletedTodo = todoDaoService.getTodoById(todo5.id)

        then:
        result.id == todo5.id
        deletedTodo.isDeleted == true
    }

    @Ignore
    void "todoService.saveList should successfully save result when params is #params"(){
        expect:
        1 == 2
    }
}
