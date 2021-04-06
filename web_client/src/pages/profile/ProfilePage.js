import { Fragment, useState } from 'react'
import { makeStyles } from '@material-ui/core/styles'
import Alert from '@material-ui/lab/Alert'
import authAdapter from '../../adapters/AuthAdapter'
import Button from '@material-ui/core/Button'
import Card from '@material-ui/core/Card'
import CardActions from '@material-ui/core/CardActions'
import CardContent from '@material-ui/core/CardContent'
import Grid from '@material-ui/core/Grid'
import Header from '../../components/header/Header'
import localizedString from '../../localization/localizedString'
import TextField from '@material-ui/core/TextField'
import Typography from '@material-ui/core/Typography'

const useStyles = makeStyles({
  card: {
    width: '100%',
    marginTop: '16px',
    backgroundColor: 'rgba(0, 132, 173, 0.07)',
  }
})

const ProfileRow = ({ titleRes, value, isEditing, setEditedValue, ...props }) => {
  const [currentValue, setCurrentValue] = useState(value)

  const handleChange = (ev) => {
    const newValue = ev.target.value
    setCurrentValue(newValue)
    setEditedValue(newValue)
  }

  return (
    <Grid container justify='flex-start' {...props}>
      <Typography
        style={{ color: '#a0a0a0', lineHeight: '40px', marginRight: '6px' }}
        variant='h6'>
        {localizedString(titleRes)}:
            </Typography>
      { !isEditing && <Typography style={{ lineHeight: '40px' }}>{value}</Typography>}
      { isEditing && <TextField onChange={handleChange} style={{ marginTop: '4px' }} value={currentValue} />}
    </Grid>
  )
}

const ServicePermissionRow = ({ permission }) => {
  return (
    <Grid container justify='space-between' style={{marginTop: '16px'}}>
      <Typography style={{lineHeight: '38px'}}>{localizedString('service')}</Typography>
      <Typography style={{lineHeight: '38px'}}>{permission.id}</Typography>
      <Alert icon={false} variant='outlined' severity='info' style={{paddingTop: '0px', paddingBottom: '0px'}}>
        {localizedString('permission_' + permission['permissionType'])}
      </Alert>
    </Grid>
  )
}

const OrganizationPermissionRow = ({ permission }) => {
  return (
    <Grid container justify='space-between' style={{marginTop: '16px'}}>
      <Typography style={{lineHeight: '38px'}}>{localizedString('organization')}</Typography>
      <Typography style={{lineHeight: '38px'}}>{permission.id}</Typography>
      <Alert icon={false} variant='outlined' severity='info' style={{paddingTop: '0px', paddingBottom: '0px'}}>
        {localizedString('permission_' + permission['permissionType'])}
      </Alert>
    </Grid>
  )
}

const PermissionRow = ({ permission }) => {
  if ('serviceId' in permission) {
    return <ServicePermissionRow permission={permission} />
  }
  return <OrganizationPermissionRow permission={permission} />
}

const createPermissionRows = () => {
  if (!authAdapter.user.wholeUserJson) {
    return null
  }
  if (!authAdapter.user.wholeUserJson['permissions']) {
    return null
  }
  const permissions = authAdapter.user.wholeUserJson['permissions']
  const result = []
  for (const elem of permissions) {
    if ('serviceId' in elem || 'organizationId' in elem) {
      result.push(elem)
    }
  }
  return result
}

export default function ProfilePage() {
  const classes = useStyles()

  const [editing, setEdit] = useState(false)

  const startEdit = () => setEdit(true)
  const stopEdit = () => setEdit(false)

  const [setEditedName] = useState(authAdapter.user.name)
  const [setEditedSurname] = useState(authAdapter.user.surname)
  const [setEditedEmail] = useState(authAdapter.user.login)

  const handleEdited = () => {
    stopEdit()
  }

  const content = [
    { titleRes: 'profile_user_name', value: authAdapter.user.name, updater: (value) => { setEditedName(value) } },
    { titleRes: 'profile_user_surname', value: authAdapter.user.surname, updater: (value) => { setEditedSurname(value) } },
    { titleRes: 'profile_user_email', value: authAdapter.user.login, updater: (value) => { setEditedEmail(value) } },
  ]

  return (
    <Fragment>
      <Header />
      <Grid container justify='center'>
        <Grid item xs={8}>
          <Grid container justify='space-between' style={{ marginTop: '16px' }}>
            <Card className={classes.card} variant='outlined'>
              <CardContent>
                {
                  content.map((elem, index) => (
                    <ProfileRow
                      key={index}
                      isEditing={editing}
                      titleRes={elem.titleRes}
                      setEditedValue={elem.updater}
                      value={elem.value} />
                  ))
                }
              </CardContent>
              <CardActions>
                <Button color='primary' onClick={startEdit}>{localizedString('edit')}</Button>
                {editing &&
                  <Button color='seconary' variant='outlined' onClick={handleEdited}>
                    {localizedString('save')}
                  </Button>
                }
              </CardActions>
            </Card>

            <Card className={classes.card} variant='outlined'>
              <CardContent>
                <Typography variant='h5'>{localizedString('profile_page_permissions')}</Typography>
                { createPermissionRows().map((elem, index) => {
                  return <PermissionRow permission={elem} key={index} />
                }) }
              </CardContent>
            </Card>
          </Grid>
        </Grid>
      </Grid>
    </Fragment>
  )
}
