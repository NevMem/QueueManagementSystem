import { Fragment, useState } from 'react'
import AddIcon from '@material-ui/icons/Add'
import Button from '@material-ui/core/Button'
import DialogActions from '@material-ui/core/DialogActions'
import DialogContent from '@material-ui/core/DialogContent'
import IconButton from '@material-ui/core/IconButton'
import localizedString from '../../localization/localizedString'
import orgAdapter from '../../adapters/OrgAdapter'
import StyledDialog from './StyledDialog'

const TimetableView = ({ timetable, updateTimetable }) => {

    const days = ['MON', 'TUE', 'WED', 'THU', 'FRI', 'SAT', 'SUN']

    const timesForDay = (day) => {
        return timetable.works.filter(elem => elem.weekday === day)
    }

    const TimeIntervalView = ({ interval }) => {
        const { from, to } = interval

        return (
            <div style={{padding: '4px', paddingLeft: '8px', paddingRight: '8px', margin: '4px', display: 'inline-block', backgroundColor: '#ffffff20', borderRadius: '4px'}}>
                {from.hour}:{from.minute}-{to.hour}:{to.minute}
            </div>
        )
    }

    const NewTimeIntervalView = ({ day }) => {

        const TimePointView = ({ timepoint, setTimepoint }) => {

            const handleHourChange = (event) => {
                setTimepoint({ ...timepoint, hour: event.target.value | 0 })
            }

            const handleMinuteChange = (event) => {
                setTimepoint({ ...timepoint, minute: event.target.value | 0 })
            }

            return (
                <div style={{display: 'inline-block'}}>
                    <input
                        onChange={handleHourChange}
                        style={{width: '16px', outline: 'none', padding: '4px', backgroundColor: '#ffffff20', border: 'none'}}
                        value={timepoint.hour} />
                    :
                    <input
                        onChange={handleMinuteChange}
                        style={{width: '16px', outline: 'none', padding: '4px', backgroundColor: '#ffffff20', border: 'none'}}
                        value={timepoint.minute} />
                </div>
            )
        }

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

        return (
            <div>
                <TimePointView timepoint={fromTimepoint} setTimepoint={setFromTimepoint} />
                -
                <TimePointView timepoint={toTimepoint} setTimepoint={setToTimepoint} />
                <IconButton onClick={handleAdd}>
                    <AddIcon />
                </IconButton>
            </div>
        )
    }

    const OneDayView = ({ day }) => {
        const times = timesForDay(day)

        return (
            <div style={{marginTop: '8px', marginBottom: '8px', display: 'flex', flexDirection: 'column'}}>
                <div style={{marginBottom: '16px'}}>{localizedString('day_name_' + day)}</div>

                <div>
                    {times.map((elem, index) => {
                        return (
                            <TimeIntervalView
                                key={index}
                                interval={{from: elem.from, to: elem.to}} />
                        )
                    })}
                    <NewTimeIntervalView day={day} />
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
