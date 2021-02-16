import React, { Component } from 'react'
import Table from '@material-ui/core/Table'
import TableBody from '@material-ui/core/TableBody'
import TableCell from '@material-ui/core/TableCell'
import TableContainer from '@material-ui/core/TableContainer'
import TableHead from '@material-ui/core/TableHead'
import TableRow from '@material-ui/core/TableRow'
import Paper from '@material-ui/core/Paper'

export default class QueueTable extends Component {
    constructor(prps) {
        super(prps)
        this.state = {
            table: [
                {ticketNumber: "T9", ets: "13:35"},
                {ticketNumber: "T10", ets: "13:38"},
                {ticketNumber: "T11", ets: "13:41"},
                {ticketNumber: "T12", ets: "13:45"},
            ]
        }
    }

    render() {
        return (
            <TableContainer component={Paper}>
                <Table aria-label="simple table">
                    <TableHead>
                        <TableRow>
                            <TableCell>Номер в очереди</TableCell>
                            <TableCell>Номер талона</TableCell>
                            <TableCell>Примерное время обслуживания</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {this.state.table.map((elem, index) => (
                            <TableRow key={index}>
                                <TableCell>{index}</TableCell>
                                <TableCell>{elem.ticketNumber}</TableCell>
                                <TableCell>{elem.ets}</TableCell>
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
            </TableContainer>
        )
    }
}
