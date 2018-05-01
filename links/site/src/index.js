import React from 'react';
import ReactDOM from 'react-dom';
import { BrowserRouter, Route, Switch } from 'react-router-dom'
import * as firebase from 'firebase/app'
import 'firebase/auth'
import 'firebase/database'
import config from './config'

firebase.initializeApp(config);

class App extends React.Component {

    constructor(props) {
        super(props)
        this.state = {
            isAuthed: false
        }
    }

    render() {
        console.log('render app', this.state)
        if (this.state.isAuthed) {
            return (
                <BrowserRouter>
                    <Switch>
                        <Route exact path="/*" component={Editor} />
                    </Switch>

                </BrowserRouter>
            )
        } else {
            return (
                <BrowserRouter>
                    <Switch>
                        <Route path="/*" component={AuthComponent} />
                    </Switch>
                </BrowserRouter>
            )
        }
    }

    componentDidMount() {
        this.unsubscribe = firebase.auth().onAuthStateChanged(user => {
            this.setState({
                isAuthed: Boolean(user) && !user.isAnonymous
            })
        })
    }

    componentWillUnmount() {
        this.unsubscribe()
    }

}

const AuthComponent = ({ }) => {
    return (
        <button onClick={onSignInClicked()}> Sign in</button>
    )
}

const onSignInClicked = () => () => {
    const provider = new firebase.auth.GoogleAuthProvider();
    firebase.auth().signInWithPopup(provider)
}

class Editor extends React.Component {

    constructor(props) {
        super(props)
        this.state = {
            data: [],
            editing: '',
            editingValue: '',
            addingKey: '',
            addingValue: ''
        }
    }

    render() {
        console.log('render editor', this.state)
        const list = this.state.data.map(each => {
            if (each.key === this.state.editing) {
                return (
                    <Edit
                        key={each.key}
                        name={each.key}
                        value={this.state.editingValue}
                        onSave={key => {
                            firebase.database()
                                .ref(`/url/${key}`)
                                .set(this.state.editingValue)
                                .then(() => {
                                    this.setState({
                                        editing: '',
                                        editingValue: ''
                                    })
                                })
                        }}
                        onChange={e => {
                            this.setState({
                                editingValue: e.target.value
                            })
                        }} />
                )
            }

            return (
                <View
                    key={each.key}
                    name={each.key}
                    value={each.value}
                    onEdit={key => {
                        this.setState({
                            editing: key,
                            editingValue: each.value
                        })
                    }}
                    onDelete={key => {
                        firebase.database()
                            .ref(`/url/${key}`)
                            .remove()
                            .then(() => {
                                this.setState({
                                    editing: '',
                                    editingValue: ''
                                })
                            })
                    }} />
            )
        })

        return (
            <div>
                {list}
                <Add
                    name={this.state.addingKey}
                    value={this.state.addingValue}
                    onAdd={key => {
                        firebase.database()
                            .ref(`/url/${encodeURIComponent(key)}`)
                            .set(this.state.addingValue)
                            .then(() => {
                                this.setState({
                                    addingKey: '',
                                    addingValue: ''
                                })
                            })
                    }}
                    onKeyChange={e => {
                        this.setState({
                            addingKey: e.target.value
                        })
                    }}
                    onValueChange={e => {
                        this.setState({
                            addingValue: e.target.value
                        })
                    }} />
            </div>
        )
    }

    componentDidMount() {
        firebase.database()
            .ref('/url')
            .once('value')
            .then(result => result.val())
            .then(result => {
                if (!result) {
                    return
                }
                const data = Object.keys(result).map(key => {
                    return {
                        key: key,
                        value: result[key]
                    }
                })
                this.setState({
                    data: data
                })
            })
    }

}

const itemViewStyle = {
    marginLeft: '50px'
}

const View = ({ name, value, onEdit, onDelete }) => (
    <div>
        <span>{decodeURIComponent(name)}</span>
        <span style={itemViewStyle}>{value}</span>
        <button
            style={itemViewStyle}
            onClick={() => onEdit(name)}>
            Edit
        </button>
        <button
            onClick={() => onDelete(name)}>
            Delete
        </button>
    </div>
)

const Edit = ({ name, value, onChange, onSave }) => (
    <div>
        <span>{decodeURIComponent(name)}</span>
        <input style={itemViewStyle} value={value} onChange={onChange} />
        <button
            style={itemViewStyle}
            onClick={() => onSave(name, value)}>
            Save
        </button>
    </div>
)

const Add = ({ name, value, onKeyChange, onValueChange, onAdd }) => (
    <div>
        <input placeholder={"/my/awesome/path"} value={name} onChange={onKeyChange} />
        <input placeholder="https://google.com" style={itemViewStyle} value={value} onChange={onValueChange} />
        <button
            style={itemViewStyle}
            onClick={() => onAdd(name, value)}>
            Add
        </button>
    </div>
)


ReactDOM.render(
    <App />,
    document.getElementById('app')
);

module.hot.accept();
