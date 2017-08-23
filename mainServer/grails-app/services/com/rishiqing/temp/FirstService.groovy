package com.rishiqing.temp

import grails.transaction.Transactional

@Transactional
class FirstService {
    def secondService
    def thirdService

    def showMe() {
        "...1... firstService from mainServer"
    }

    def showManCount(){
        "...1... firstMan count is ${FirstMan.count()}"
    }

    def showSecondService(){
        "...1... ${secondService.showMe()}"
    }

    def showSecondMan(){
        "...1... ${secondService.showManCount()}"
    }

    def showThirdService(){
        "...1... ${thirdService.showMe()}"
    }

    def showThirdMan(){
        "...1... ${thirdService.showManCount()}"
    }
}
