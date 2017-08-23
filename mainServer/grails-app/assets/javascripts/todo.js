// Full spec-compliant TodoMVC with localStorage persistence
// and hash-based routing in ~120 effective lines of JavaScript.

// localStorage persistence
var STORAGE_KEY = 'todos-vuejs-2.0'

var isWatchTodos = true

function watchTodos(){
    return isWatchTodos
}
var todoStorage = {
    fetchList: function (cb) {
        Vue.http.get('/mainServer/todo/fetchTodoList').then(function(res){
            var json = res.body
            cb(json.result)
        }, function(err){
            alert('error...')
        });
        // return;
        // var todos = JSON.parse(localStorage.getItem(STORAGE_KEY) || '[]')
        // todos.forEach(function (todo, index) {
        //     todo.id = index
        // })
        // todoStorage.uid = todos.length
        // return todos
    },
    save: function(todo, cb){
        Vue.http.post('/mainServer/todo/saveTodo', todo).then(function(res){
            var json = res.body
            cb(json.result)
        }, function(err){
            alert('error...')
        });
    },
    remove: function(todo, cb){
        Vue.http.post('/mainServer/todo/removeTodo', todo).then(function(res){
            var json = res.body
            cb(json.result)
        }, function(err){
            alert('error...')
        });
    }
    // save: function (todos) {
    //     Vue.http.post('/mainServer/todo/saveTodoList', {list: todos}).then(function(res){
    //         var json = res.body
    //         app.todos = json.result
    //         app.toSync = false
    //     }, function(err){
    //         alert('error...')
    //     });
    //     // localStorage.setItem(STORAGE_KEY, JSON.stringify(todos))
    // }
}

// visibility filters
var filters = {
    all: function (todos) {
        return todos
    },
    active: function (todos) {
        return todos.filter(function (todo) {
            return !todo.isDone
        })
    },
    completed: function (todos) {
        return todos.filter(function (todo) {
            return todo.isDone
        })
    }
}

// app Vue instance
var app = new Vue({
    // app initial state
    data: {
        todos: [],
        newTodo: '',
        editedTodo: null,
        visibility: 'all',
        toSync: false
    },

    // watch todos change for localStorage persistence
    watch: {
        // todos: {
        //     handler: function (todos) {
        //         if(this.toSync){
        //             todoStorage.save(todos)
        //         }
        //     },
        //     deep: true
        // }
    },

    // computed properties
    // http://vuejs.org/guide/computed.html
    computed: {
        filteredTodos: function () {
            return filters[this.visibility](this.todos)
        },
        remaining: function () {
            return filters.active(this.todos).length
        },
        allDone: {
            get: function () {
                return this.remaining === 0
            },
            set: function (value) {
                this.todos.forEach(function (todo) {
                    todo.isDone = value
                })
            }
        }
    },

    filters: {
        pluralize: function (n) {
            return n === 1 ? 'item' : 'items'
        }
    },

    // methods that implement data logic.
    // note there's no DOM manipulation here at all.
    methods: {
        addTodo: function () {
            var value = this.newTodo && this.newTodo.trim()
            if (!value) {
                return
            }
            var obj = {
                title: value,
                isDone: false
            }
            var that = this
            todoStorage.save(obj, function(todo){
                that.todos.push(todo)
            })
            this.newTodo = ''
        },

        removeTodo: function (todo) {
            var that = this
            todoStorage.remove(todo, function(){
                that.todos.splice(that.todos.indexOf(todo), 1)
            })
        },

        editTodo: function (todo) {
            this.beforeEditCache = todo.title
            this.editedTodo = todo
        },

        doneEdit: function (todo) {
            if (!this.editedTodo) {
                return
            }
            this.editedTodo = null
            todo.title = todo.title.trim()
            if (!todo.title) {
                this.removeTodo(todo)
            }
            todoStorage.save(todo, function(){});
        },

        doneTodo: function(todo){
            var newTodo = JSON.parse(JSON.stringify(todo))
            newTodo.isDone = !todo.isDone

            todoStorage.save(newTodo, function(dbTodo){
                todo.isDone = dbTodo.isDone
            })
        },

        cancelEdit: function (todo) {
            this.editedTodo = null
            todo.title = this.beforeEditCache
        },

        removeCompleted: function () {
            var doneList = filters.completed(this.todos)
            var that = this
            doneList.forEach(function(todo){
                that.removeTodo(todo)
            })
            // this.todos = filters.active(this.todos)
        }
    },

    // a custom directive to wait for the DOM to be updated
    // before focusing on the input field.
    // http://vuejs.org/guide/custom-directive.html
    directives: {
        'todo-focus': function (el, binding) {
            if (binding.value) {
                el.focus()
            }
        }
    }
})

// handle routing
function onHashChange () {
    var visibility = window.location.hash.replace(/#\/?/, '')
    if (filters[visibility]) {
        app.visibility = visibility
    } else {
        window.location.hash = ''
        app.visibility = 'all'
    }
}

window.addEventListener('hashchange', onHashChange)
onHashChange()

// mount
app.$mount('.todoapp')

todoStorage.fetchList(function(list){
    app.todos = list
})
