import React, { Component, Fragment } from 'react'
import Header from '../../components/header/Header'
import Grid from '@material-ui/core/Grid'
import Typography from '@material-ui/core/Typography'
import AddButton from '../../components/buttons/add_button/AddButton'
import OrganizationCard from '../../components/organization/OrganizationCard'

export default class HomePage extends Component {
    constructor(prps) {
        super(prps)
        this.state = {}
    }
    
    render() {

        const organizationsData = [
            {
                id: 'sber-1',
                name: 'Sber #1',
                services: [
                    {
                        id: 'sber-service-1',
                        name: 'Кредиты',
                        avgServiceTime: '~1:30 мин/чел',
                        waiting: '10 человек ожидают',
                        rating: '4.0',
                        isWorking: true,
                        queues: [
                            {
                                id: 'sber-service-1-queue-1',
                                name: 'Окно 1',
                                waiting: '3 человека',
                                avgServiceTime: '~2:10 мин/чел',
                            },
                            {
                                id: 'sber-service-1-queue-2',
                                name: 'Окно 2',
                                waiting: '4 человека',
                                avgServiceTime: '~1:20 мин/чел',
                            },
                            {
                                id: 'sber-service-1-queue-3',
                                name: 'Окно 3',
                                waiting: '3 человека',
                                avgServiceTime: '~0:30 мин/чел',
                            }
                        ]
                    }
                ]
            },
            {
                id: 'some-id-1',
                name: 'Аптека на Тверской',
                services: [
                    {
                        id: 'service-1-1',
                        name: 'Кафе',
                        avgServiceTime: '~1:30 мин/чел',
                        waiting: '10 человек ожидают',
                        rating: '4.0',
                        isWorking: true
                    },
                    {
                        id: 'service-1-2',
                        name: 'Таблетки',
                        avgServiceTime: '~0:40 мин/чел',
                        waiting: '2 человек ожидают',
                        rating: '4.9',
                        isWorking: true
                    },
                ]
            },
            {
                id: 'some-id-2',
                name: 'Аптека на Баумана (Казань)',
                services: [
                    {
                        id: 'service-2-1',
                        name: 'Кофепоинт',
                        avgServiceTime: '~2:30 мин/чел',
                        waiting: '5 человек ожидают',
                        rating: '4.3',
                        isWorking: true
                    },
                    {
                        id: 'service-2-2',
                        name: 'Таблетки',
                        avgServiceTime: '~0:40 мин/чел',
                        waiting: '0 человек ожидают',
                        rating: '3.0',
                        isWorking: true
                    },
                    {
                        id: 'service-2-3',
                        name: 'Варенье',
                        avgServiceTime: '~0:20 мин/чел',
                        waiting: '20 человек ожидают',
                        rating: '4.99',
                        isWorking: true
                    },
                ]
            }
        ]

        return (
            <Fragment>
                <Header />
                <Grid container justify='center'>
                    <Grid item xs={8}>
                        <Grid container justify='space-between' style={{marginTop: '16px'}}>
                            <Typography style={{color: '#a0a0a0', fontSize: '26px'}} variant='body2'>
                                Ваши организации
                            </Typography>
                            <AddButton isPrimaryButton={true} text='Новая организация' />
                        </Grid>
                        {/* <OrganizationCard style={{marginTop: '20px'}}  /> */}
                        {/* <OrganizationCard style={{marginTop: '20px'}} /> */}
                        { organizationsData.map((elem) => {
                            return <OrganizationCard key={elem.id} organizationData={elem} style={{marginTop: '20px'}}  />
                        }) }
                    </Grid>
                </Grid>
            </Fragment>
        )
    }
}
