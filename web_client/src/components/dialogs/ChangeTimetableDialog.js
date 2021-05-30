import { Fragment, useState } from 'react'
import AddIcon from '@material-ui/icons/Add'
import Button from '@material-ui/core/Button'
import Delete from '@material-ui/icons/Delete'
import DialogActions from '@material-ui/core/DialogActions'
import DialogContent from '@material-ui/core/DialogContent'
import IconButton from '@material-ui/core/IconButton'
import localizedString from '../../localization/localizedString'
import orgAdapter from '../../adapters/OrgAdapter'
import StyledDialog from './StyledDialog'

const NewTimeIntervalView = ({ day, timetable, updateTimetable }) => {

    const [fromTimepoint, setFromTimepoint] = useState({hour: undefined, minute: undefined})
    const [toTimepoint, setToTimepoint] = useState({hour: undefined, minute: undefined})

    const handleAdd = () => {
        if (fromTimepoint.hour === undefined
            || fromTimepoint.minute === undefined
            || toTimepoint.hour === undefined
            || toTimepoint.minute === undefined) {
                alert('Some field is missing')
                return
            }

        updateTimetable({
            works: [
                ...timetable.works,
                { weekday: day, from: { ...fromTimepoint }, to: { ...toTimepoint } }
            ]
        })
    }

    const handleFromHourChange = (event) => {
        setFromTimepoint({ ...fromTimepoint, hour: event.target.value | 0 })
    }

    const handleFromMinuteChange = (event) => {
        setFromTimepoint({ ...fromTimepoint, minute: event.target.value | 0 })
    }

    const handleToHourChange = (event) => {
        setToTimepoint({ ...toTimepoint, hour: event.target.value | 0 })
    }

    const handleToMinuteChange = (event) => {
        setToTimepoint({ ...toTimepoint, minute: event.target.value | 0 })
    }

    return (
        <div>
            <input
                onChange={handleFromHourChange}
                style={{width: '16px', outline: 'none', padding: '4px', backgroundColor: '#ffffff20', border: 'none'}}
                value={fromTimepoint.hour} />
            :
            <input
                onChange={handleFromMinuteChange}
                style={{width: '16px', outline: 'none', padding: '4px', backgroundColor: '#ffffff20', border: 'none'}}
                value={fromTimepoint.minute} />
            -
            <input
                onChange={handleToHourChange}
                style={{width: '16px', outline: 'none', padding: '4px', backgroundColor: '#ffffff20', border: 'none'}}
                value={toTimepoint.hour} />
            :
            <input
                onChange={handleToMinuteChange}
                style={{width: '16px', outline: 'none', padding: '4px', backgroundColor: '#ffffff20', border: 'none'}}
                value={toTimepoint.minute} />
            <IconButton onClick={handleAdd}>
                <AddIcon />
            </IconButton>
        </div>
    )
}

const TimetableView = ({ timetable, updateTimetable }) => {

    const days = ['MON', 'TUE', 'WED', 'THU', 'FRI', 'SAT', 'SUN']

    const timesForDay = (day) => {
        return timetable.works.filter(elem => elem.weekday === day)
    }

    const TimeIntervalView = ({ interval, deleteInterval }) => {
        const { from, to } = interval

        const handleDelete = () => {
            deleteInterval(interval)
        }

        return (
            <div style={{display: 'inline-block'}}>
                <div style={{
                    padding: '4px',
                    paddingLeft: '8px',
                    paddingRight: '8px',
                    margin: '4px',
                    display: 'inline-block',
                    backgroundColor: '#ffffff20',
                    borderRadius: '4px',
                    cursor: 'pointer'
                }}>
                    {from.hour}:{from.minute}-{to.hour}:{to.minute}
                </div>
                <Delete
                    style={{position: 'relative', top: '-4px', right: '14px', cursor: 'pointer'}}
                    onClick={handleDelete} />
            </div>
        )
    }

    const OneDayView = ({ day }) => {
        const times = timesForDay(day)

        const handleDelete = (interval) => {
            const newWorks = timetable.works.filter(elem => elem !== interval)
            updateTimetable({
                works: [ ...newWorks ]
            })
        }

        return (
            <div style={{marginTop: '8px', marginBottom: '8px', display: 'flex', flexDirection: 'column'}}>
                <div style={{marginBottom: '16px'}}>{localizedString('day_name_' + day)}</div>

                <div>
                    {times.map((elem, index) => {
                        return (
                            <TimeIntervalView
                                key={index}
                                deleteInterval={handleDelete}
                                interval={elem} />
                        )
                    })}
                    <NewTimeIntervalView day={day} timetable={timetable} updateTimetable={updateTimetable} />
                </div>
            </div>
        )
    }

    return (
        <Fragment>
            { days.map((elem, index) => {
                return <OneDayView key={index} day={elem} />
            }) }
        </Fragment>
    )
}

export default function ChangeTimetableDialog({ organization, onClose, open }) {

    const [timetable, setTimetable] = useState(organization.timetable)

    const updateTimetable = (table) => {
        setTimetable(table)
    }

    const handleOk = () => {
        orgAdapter.updateOrganization(
            organization.id,
            organization.name,
            organization.address,
            organization.data,
            timetable)
            .then(data => {
                handleClose()
            })
            .catch(err => {
                alert(err)
                alert(JSON.stringify(err))
            })
    }

    const handleClose = () => {
        onClose()
    }

    return (
        <StyledDialog open={open} onClose={handleClose}>
            <DialogContent style={{minWidth: '600px'}}>
                <TimetableView timetable={timetable} updateTimetable={updateTimetable} />
            </DialogContent>
            <DialogActions>
                <Button onClick={handleClose}>
                    {localizedString('cancel')}
                </Button>
                <Button onClick={handleOk}>
                    {localizedString('ok')}
                </Button>
            </DialogActions>
        </StyledDialog>
    )
}
