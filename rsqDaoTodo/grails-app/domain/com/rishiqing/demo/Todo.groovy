package com.rishiqing.demo

class Todo {

    String title
    Boolean isDone = false
    Long doneTime
    Boolean isDeleted = false
    Long deletedTime

    Date dateCreated
    Date lastUpdated

    static constraints = {
        doneTime nullable: true
        deletedTime nullable: true
    }

    Map toMap(){
        return [
                id: id,
                title: title,
                isDone: isDone,
                doneTime: doneTime,
                isDeleted: isDeleted,
                deletedTime: deletedTime
        ]
    }
}
