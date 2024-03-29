import './OrganizationCard.css'
import { Link } from 'react-router-dom'
import { useState, Component } from 'react'
import AddButton from '../../components/buttons/add_button/AddButton'
import AddServiceDialog from '../dialogs/AddServiceDialog'
import authAdapter from '../../adapters/AuthAdapter'
import Button from '@material-ui/core/Button'
import ChangeTimetableDialog from '../dialogs/ChangeTimetableDialog'
import Edit from '@material-ui/icons/Edit'
import EditOrganizationDialog from '../dialogs/EditOrganizationDialog'
import feedbackAdapter from '../../adapters/FeedbackAdapter'
import Grid from '@material-ui/core/Grid'
import IconButton from '@material-ui/core/IconButton'
import localizedString from '../../localization/localizedString'
import ServiceRow from '../service-row/ServiceRow'
import Table from '../../images/table.svg'
import Typography from '@material-ui/core/Typography'

class FeedbackCountRow extends Component {

    constructor(prps) {
        super(prps)
        this.state = {
            loading: true,
            count: undefined
        }
    }

    componentDidMount() {
        feedbackAdapter.loadFeedback(this.props.entityId)
            .then(data => {
                this.setState(state => { return { ...state, count: data.length, loading: false } })
            })
    }

    render() {
        const commonStyle = {
            fontSize: '16px',
            color: '#a0a0a0',
            marginTop: '8px'
        }

        const loadedStyle = {
            ...commonStyle,
            color: '#3070ff',
            textDecoration: 'none'
        }

        if (this.state.loading === true) {
            return <Typography style={commonStyle} variant='h6'>{localizedString('loading')}</Typography>
        }

        return (
            <Typography style={loadedStyle} variant='h6'>
                <Link style={loadedStyle} to={'/feedback/' + this.props.entityId}>
                    {this.state.count} {localizedString('feedback')}
                </Link>
            </Typography>
        )
    }
}

export default function OrganizationCard({organizationData, ...props}) {
    const { name, address, services, admins } = organizationData

    const [open, setOpen] = useState(false)
    const [editDialogOpen, setOpenEditDialog] = useState(false)
    const [timetableDialogOpen, setTimetableDialogOpened] = useState(false)

    const handleOpen = () => {
        setOpen(true)
    }

    const handleClose = () => {
        setOpen(false)
    }

    const handleStartEdit = () => {
        setOpenEditDialog(true)
    }

    const handleStopEdit = () => {
        setOpenEditDialog(false)
    }

    const handleTimetableClose = () => {
        setTimetableDialogOpened(false)
    }

    const openTimetable = () => {
        setTimetableDialogOpened(true)
    }

    const self = admins.find(admin => {
        return admin.email === authAdapter.user.login
    })

    const hasTimetable = organizationData.timetable !== undefined && organizationData.timetable.works.length > 0

    return (
        <div className = 'organizationCard' {...props}>
            <div style={{display: 'flex', flexDirection: 'row', justifyContent: 'space-between'}}>
                <div style={{display: 'flex', flexDirection: 'row'}}>
                    <Typography style={{color: '#c0c0c0', fontSize: '30px'}} variant='body2'>
                        {name}
                    </Typography>
                    <div>
                        <IconButton onClick={handleStartEdit}>
                            <Edit style={{width: '16px', height: '16px'}} />
                        </IconButton>
                    </div>
                    <EditOrganizationDialog
                        open={editDialogOpen}
                        onClose={handleStopEdit}
                        organization={organizationData} />
                    { !hasTimetable &&
                        <div>
                            <Button onClick={openTimetable} style={{color: '#a0a0a0', fontSize: '14px'}} variant='text'>
                                {localizedString('setup_timetable')}
                            </Button>
                            <ChangeTimetableDialog
                                organization={organizationData}
                                open={timetableDialogOpen}
                                onClose={handleTimetableClose} />
                        </div>
                    }
                    { hasTimetable &&
                        <div>
                            <Button onClick={openTimetable} style={{color: '#a0a0a0', fontSize: '14px'}} variant='text'>
                                {localizedString('edit_timetable')}
                            </Button>
                            <ChangeTimetableDialog
                                organization={organizationData}
                                open={timetableDialogOpen}
                                onClose={handleTimetableClose} />
                        </div>
                    }
                </div>
                <div style={{display: 'flex', flexDirection: 'row'}}>
                    <Link to={'/table/' + organizationData.id}>
                        <IconButton style={{marginRight: '16px'}}>
                            <img src={Table} alt='table button' style={{width: '16px'}} />
                        </IconButton>
                    </Link>

                    { self !== undefined && ['OWNER', 'MANAGER'].indexOf(self.permissionType) >= 0 &&
                        <div>
                            <AddButton
                                onClick={handleOpen}
                                isPrimaryButton={false}
                                text={localizedString('new_service')} />
                        </div>
                    }
                    <AddServiceDialog open={open} onClose={handleClose} organization={organizationData} />
                </div>
            </div>
            <Grid container>
                <Typography style={{color: '#a0a0a0', fontSize: '20px'}} variant='body2'>
                    {address}
                </Typography>
            </Grid>
            <Grid container>
                <Typography style={{color: '#a0a0a0', fontSize: '18px'}} variant='body2'>
                    {admins.length} {localizedString('employee')}
                </Typography>
            </Grid>
            <Grid container>
                <FeedbackCountRow entityId={'organization_' + organizationData.id} />
            </Grid>
            <Grid container style={{marginTop: '20px', marginBottom: '20px'}}>
                <Grid item>
                    <Typography style={{color: '#a0a0a0', fontSize: '24px'}} variant='body2'>
                        {services.length > 0 && localizedString('all_services')}
                        {services.length === 0 && 'У вас нет сервисов в данной организации'}
                    </Typography>
                </Grid>
            </Grid>
            { services.map(elem => {
                return <ServiceRow key={elem.id} organizationId={organizationData.id} serviceData={elem} />
            }) }
        </div>
    )
}
