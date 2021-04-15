import { Component } from 'react'
import orgAdapter from '../../adapters/OrgAdapter'
import Grid from '@material-ui/core/Grid'
import triangle from '../../images/triangle.svg'

const styles = {
    waitingBlock: {
        display: 'flex',
        flexDirection: 'column',
        flexWrap: 'wrap',
        padding: '32px',
        height: 'calc(100vh - 64px)',
        alignItems: 'flex-start',
    },
    waitingTicket: {
        color: 'black',
        fontSize: '36px',
        margin: '16px',
        backgroundColor: '#30de50f0',
        borderRadius: '8px',
        padding: '16px',
        display: 'inline-block',
    },
    processingBlock: {
        display: 'flex',
        flexDirection: 'column-reverse',
        flexWrap: 'wrap',
        padding: '32px',
        height: 'calc(100vh - 64px)',
        alignItems: 'flex-start',
    },
    processingTicket: {
        color: 'black',
        fontSize: '36px',
        margin: '16px',
        padding: '16px',
        paddiggLeft: '30px',
        paddiggRight: '30px',
        display: 'flex',
        flexDirection: 'row',
        backgroundImage: 'linear-gradient(to right, #1E2434, #2E3444, #2E3444, #2E3444, #2E3444, #30de5000)'
    },
    triangleStyle: {
        width: '20px',
        marginLeft: '10px',
        marginRight: '10px',
    },
    processingText: {
        fontSize: '36px',
        color: 'white',
    },
}

const WatingTicket = ({ ticket }) => {
    return (
        <div style={styles.waitingTicket}>
            <p style={{display: 'inline-block'}}>{ticket.ticketId}</p>
        </div>
    )
}

const WaitingBlock = ({ tickets }) => {
    const waiting = tickets.filter(ticket => ticket.state === 'WAITING').reverse()
    
    return (
        <div style={styles.waitingBlock}>
            {waiting.map(elem => {
                return (
                    <WatingTicket ticket={elem} key={elem.ticketId} />
                )
            })}
        </div>
    )
}

const ProcessingTicket = ({ ticket }) => {
    return (
        <div style={styles.processingTicket}>
            <div style={styles.processingText}>{ticket.window}</div>
            <img style={styles.triangleStyle} src={triangle} alt='triangle' />
            <div style={styles.processingText}>{ticket.ticketId}</div>
        </div>
    )
}

const ProcessingBlock = ({ tickets }) => {
    const processing = tickets.filter(ticket => ticket.state === 'PROCESSING').reverse()

    return (
        <div style={styles.processingBlock}>
            {processing.map(elem => {
                return (
                    <ProcessingTicket ticket={elem} key={elem.ticketId} />
                )
            })}
        </div>
    )
}

export default class TablePage extends Component {
    constructor(prps) {
        super(prps)
        this.state = {
            organizationId: this.props.location.pathname.split('/').pop(),
            loading: true,
            error: undefined,
            data: { tickets: [] },
            updateId: 0
        }
    }

    componentDidMount() {
        this.updateAndReschedule()
    }

    componentWillUnmount() {
        clearTimeout(this.state.updateId)
    }

    updateAndReschedule() {
        orgAdapter.tickets(this.state.organizationId)
            .then(data => {
                console.log(data)
                if (data.tickets !== undefined) {
                    this.setState(state => { return { ...state, data: data, error: undefined } })
                }
                this.scheduleUpdate()
            })
            .catch(err => {
                this.setState(state => { return { ...state, error: JSON.stringify(err) } })
                this.scheduleUpdate()
            })
    }

    scheduleUpdate() {
        const updateId = setTimeout(this.updateAndReschedule.bind(this), 1000)
        this.setState(state => { return { ...state, updateId: updateId } })
    }

    render() {
        return (
            <Grid container style={{height: '100vh'}}>
                <Grid item xs={6}>
                    <ProcessingBlock tickets={this.state.data.tickets} />
                </Grid>

                <Grid item xs={6}>
                    <WaitingBlock tickets={this.state.data.tickets} />
                </Grid>
            </Grid>
        )
    }
}
