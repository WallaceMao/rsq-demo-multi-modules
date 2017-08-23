package com.rishiqing.demo.dao

import com.rishiqing.demo.Todo
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import grails.validation.ValidationException
import spock.lang.Specification
import spock.lang.Unroll

/**
 * TodoDaoService的单元测试
 */
@TestFor(TodoDaoService)
@Mock(Todo)
class TodoDaoServiceSpec extends Specification {

    def setup() {
    }

    def cleanup() {
    }

    @Unroll
    void "It should throw ValidateException when fields are #newTodo"(){
        when:
        service.saveOrUpdate(new Todo(newTodo))

        then:
        ValidationException e = thrown()

        where:
        newTodo << [[title: null]]
    }

    void "test save user when user.id is null"() {
        given:
        String strTitle = 'aaaaaa'
        Todo newTodo = new Todo(title: strTitle)

        when:
        service.saveOrUpdate(newTodo)

        then:
        Todo.count() == 1
        newTodo.id == 1L
        newTodo.title == strTitle
        !newTodo.isDone
        newTodo.doneTime == null
        !newTodo.isDeleted
        newTodo.deletedTime == null
        newTodo.dateCreated != null
        newTodo.lastUpdated != null
    }

    void "test update user when user.id is not null"(){
        given:
        Date now = new Date()
        def (newTitle, newIsDone, newDoneTime, newIsDeleted, newDeletedTime) =
        ['bbbbbb', true, now.getTime(), true, now.getTime()]
        String strTitle = 'aaaaaa'
        Todo newTodo = new Todo(title: strTitle)
        service.saveOrUpdate(newTodo)

        when: 'change title'
        newTodo.title = newTitle
        newTodo = service.saveOrUpdate(newTodo)

        then: 'validate title'
        newTodo.title == newTitle

        when: 'change done status'
        newTodo.isDone = newIsDone
        newTodo.doneTime = newDoneTime
        newTodo = service.saveOrUpdate(newTodo)

        then: 'validate done status'
        newTodo.isDone == newIsDone
        newTodo.doneTime == newDoneTime

        when: 'change delete status'
        newTodo.isDeleted = newIsDeleted
        newTodo.deletedTime = newDeletedTime
        newTodo = service.saveOrUpdate(newTodo)

        then: 'validate delete status'
        newTodo.isDeleted == newIsDeleted
        newTodo.deletedTime == newDeletedTime
    }

    void "test remove user"(){
        given:
        String strTitle = 'aaaaaa'
        Todo newTodo = new Todo(title: strTitle)
        service.saveOrUpdate(newTodo)

        when: 'remove a todo without id'
        Todo removed = service.remove(new Todo(title: 'sssss'))

        then:
        removed == null
        service.getTodoList().size() == 1

        when: 'remove a todo'
        removed = service.remove(newTodo)

        then:
        removed == newTodo
        service.getTodoList().size() == 0

        when: 'remove a todo already deleted'
        removed = service.remove(newTodo)

        then:
        removed == newTodo
        service.getTodoList().size() == 0
    }

    void "test getTodoList"(){
        given:
        def (strTitle1, strTitle2) = ['aaaaaa','bbbbbb']
        Todo newTodo1 = new Todo(title: strTitle1)
        service.saveOrUpdate(newTodo1)
        Todo newTodo2 = new Todo(title: strTitle2)
        service.saveOrUpdate(newTodo2)

        when:
        List list = service.getTodoList()

        then:
        list.size() == 2
    }

    void "test getTodoById"(){
        given:
        def (strTitle1, strTitle2) = ['aaaaaa','bbbbbb']
        Todo newTodo1 = new Todo(title: strTitle1)
        service.saveOrUpdate(newTodo1)
        Todo newTodo2 = new Todo(title: strTitle2)
        service.saveOrUpdate(newTodo2)

        when:
        Todo todoFromDb = service.getTodoById(newTodo1.id)

        then:
        todoFromDb.id == newTodo1.id
        todoFromDb.title == newTodo1.title
    }
}
