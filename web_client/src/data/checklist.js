import { makeAutoObservable } from 'mobx'

export default class Checklist {
    constructor() {
        this.list = []
        this.id = 0
        makeAutoObservable(this)
    }

    getList() {
        return this.list
    }

    addItem(title) {
        const newList = [...this.list]
        newList.push({
            id: this.id,
            name: title
        })
        this.list = newList
        this.id += 1
    }

    resetItemsWith(list) {
        this.list = []
        for (const elem of list) {
            this.addItem(elem)
        }
    }

    removeItem(item) {
        const index = this.list.indexOf(item)
        if (index < 0) {
            return
        }
        this.list.splice(index, 1)
    }
}
