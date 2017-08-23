package com.rishiqing.demo.web

import geb.spock.GebSpec
import org.openqa.selenium.Keys
import spock.lang.Shared
import spock.lang.Stepwise

/**
 * Created by  on 2017/8/21.Wallace
 */
@Stepwise
class TodoWebFunctionalSpec extends GebSpec {

    @Shared String todoTitle = 'lastTodo'
    @Shared String todoTitle2 = 'lastTodoAppend'

    def setup(){}
    def cleanup(){}

    def "test create todo"() {
        when: 'go to app page'
        to TodoAppPage

        and: 'create last todo'
        createTodo todoTitle

        then:
        waitFor(2){
            todoList.$('li')[-1].$('.view label').text() == todoTitle
        }
    }

    def "test edit todo"(){
        when: 'go to app page'
        to TodoAppPage
        waitFor(2){
            todoList.$('li').size() > 0
        }

        and: 'define variables'
        def lastTodo = todoList.$('li')[-1]
        def lastTodoLabel = lastTodo.$('.view label')
        def lastTodoEdit = lastTodo.$('.edit')

        and: 'double to edit last todo'
        interact { doubleClick(lastTodoLabel) }

        then:
        lastTodoEdit.displayed == true

        when: 'append title and enter'
        lastTodoEdit << todoTitle2
        lastTodoEdit << Keys.ENTER

        then:
        waitFor(3){
            lastTodoLabel.text() == "$todoTitle$todoTitle2"
        }
    }

    def "test check todo"(){
        when: 'go to app page'
        to TodoAppPage
        waitFor(2){
            todoList.$('li').size() > 0
        }

        and: 'define variables'
        def lastTodo = todoList.$('li')[-1]
        def lastTodoLabel = lastTodo.$('.view label')
        def lastTodoCheckout = lastTodo.$('.view input.toggle')


        and: 'checkout last todo'
        lastTodoCheckout.click()

        then:
        waitFor(2){
            lastTodo.classes().indexOf('completed') != -1
        }
    }

    def "test delete todo"(){
        when: 'go to app page'
        to TodoAppPage
        waitFor(2){
            todoList.$('li').size() > 0
        }

        and: 'define variables'
        def lastTodo = todoList.$('li')[-1]
        long oldSize = todoList.$('li').size()
        def lastTodoDelete = lastTodo.$('.view button.destroy')


        and: 'delete last todo'
        interact { moveToElement(lastTodo) }
        lastTodoDelete.click()

        then:
        waitFor(2){
            todoList.$('li').size() == oldSize - 1
        }
    }
}
