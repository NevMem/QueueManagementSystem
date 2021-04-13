import { Fragment, useState } from 'react'
import { observer } from 'mobx-react'
import AddIcon from '@material-ui/icons/Add'
import DeleteIcon from '@material-ui/icons/Delete'
import Grid from '@material-ui/core/Grid'
import IconButton from '@material-ui/core/IconButton'
import InputAdornment from '@material-ui/core/InputAdornment'
import localizedString from '../../localization/localizedString'
import OutlinedInput from '@material-ui/core/OutlinedInput'
import Typography from '@material-ui/core/Typography'

const ChecklistBlock = observer(({ checklist }) => {

    const [value, setValue] = useState('')
    const handleChange = (ev) => {
        setValue(ev.target.value)
    }
    const addItem = () => {
        checklist.addItem(value)
        setValue('')
    }
    const handleDelete = (item) => {
        checklist.removeItem(item)
    }

    return (
        <Fragment>
            <Typography
                style={{marginTop: '12px', marginBottom: '12px'}}>
                    {localizedString('checklist_header')}
            </Typography>
            { checklist.getList().map((elem) => {
                return (
                    <Grid container key={elem.id} justify='space-between'>
                        <Typography style={{lineHeight: '48px'}}>{elem.name}</Typography>
                        <IconButton
                            aria-label='delete checklist item'
                            onClick={handleDelete.bind(elem, elem)}>
                                <DeleteIcon />
                        </IconButton>
                    </Grid>
                )
            }) }
            <Grid container>
                <OutlinedInput
                    margin='dense'
                    value={value}
                    style={{width: '100%'}}
                    endAdornment={
                        <InputAdornment position='end'>
                            <IconButton
                                aria-label="add item to checklist"
                                onClick={addItem}
                                edge="end">
                                <AddIcon />
                            </IconButton>
                        </InputAdornment>
                    }
                    onChange={handleChange} />
            </Grid>
        </Fragment>
    )
})

export default ChecklistBlock