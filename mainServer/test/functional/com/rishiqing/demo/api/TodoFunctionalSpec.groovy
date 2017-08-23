package com.rishiqing.demo.api

import com.rishiqing.demo.util.http.RsqRestRequest
import com.rishiqing.demo.util.http.RsqRestResponse
import com.rishiqing.demo.util.http.RsqRestUtil
import geb.spock.GebSpec
import spock.lang.Shared
import spock.lang.Stepwise

/**
 * Created by  on 2017/8/15.Wallace
 */
@Stepwise
class TodoFunctionalSpec extends GebSpec {

    @Shared String baseUrl
    @Shared String path

    @Shared def todoForUpdate

    @Shared long orgListSize

    def setupSpec(){

        baseUrl = ConfigUtil.config.baseUrl
        path = ConfigUtil.config.path

//        RsqRestUtil.config([proxy: ['127.0.0.1': 5555]])

    }

    def setup(){
    }

    def cleanup(){}

    def "test get all todo list"(){
        when:
        RsqRestResponse resp = RsqRestUtil.get("${baseUrl}${path}todo/fetchTodoList"){}
        orgListSize = resp.json.result.size()

        then:
        resp.status == 200
        resp.json.errcode == 0
    }

    def "test save new todo"(){
        when:
        String title = "new Todo ${new Date()}"
        def params = [
                title: title
        ]
        RsqRestResponse resp = RsqRestUtil.post("${baseUrl}${path}todo/saveTodo"){
            fields params
        }
        todoForUpdate = resp.json.result

        then:
        resp.status == 200
        resp.json.errcode == 0
        resp.json.result.id != null
        resp.json.result.title == title

        when:
        resp = RsqRestUtil.get("${baseUrl}${path}todo/fetchTodoList"){}

        then:
        resp.status == 200
        resp.json.errcode == 0
        resp.json.result.size() == orgListSize + 1
        resp.json.result[-1].title == title
    }

    def "test update todo"(){
        when:
        String title = "changed Todo ${new Date()}"
        def params = [
                id: todoForUpdate.id,
                title: title
        ]
        RsqRestResponse resp = RsqRestUtil.post("${baseUrl}${path}todo/saveTodo"){
            fields params
        }

        then:
        resp.status == 200
        resp.json.errcode == 0
        resp.json.result.id != null
        resp.json.result.title == title

        when:
        resp = RsqRestUtil.get("${baseUrl}${path}todo/fetchTodoList"){}

        then:
        resp.status == 200
        resp.json.errcode == 0
        resp.json.result.size() == orgListSize + 1
        resp.json.result[-1].title == title
    }

    def "test remove todo"(){
        when:
        def params = [
                id: todoForUpdate.id,
        ]
        RsqRestResponse resp = RsqRestUtil.post("${baseUrl}${path}todo/removeTodo"){
            fields params
        }

        then:
        resp.status == 200
        resp.json.errcode == 0
        resp.json.result.id == todoForUpdate.id

        when:
        resp = RsqRestUtil.get("${baseUrl}${path}todo/fetchTodoList"){}

        then:
        resp.status == 200
        resp.json.errcode == 0
        resp.json.result.size() == orgListSize
    }
}
