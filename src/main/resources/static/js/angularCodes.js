var lubricationSystemApp = angular.module('lubricationSystemApp', ['ngMap', 'ui.bootstrap', 'scrollable-table', 'angular-dialgauge', 'ngRadialGauge', 'ngAnimate']);

lubricationSystemApp.config(['$httpProvider', function ($httpProvider) {
        delete $httpProvider.defaults.headers.common['X-Requested-With'];
    }
]);

lubricationSystemApp.factory('httpReq', function ($http, $q) {
    return {
        queryLSs: function () {
            return $http.get('/ls?category=all')
                    .then(function (response) {
                        return response.data;
                    }, function (response) {
                        return $q.reject(response.data);
                    });
        },
        queryIndividualLUs: function (luId) {
            return $http.get('/lu/' + luId + '/info')
                    .then(function (response) {
                        return response.data;
                    }, function (response) {
                        return $q.reject(response.data);
                    });
        },
        queryLUs: function () {
            return $http.get('/lu')
                    .then(function (response) {
                        return response.data;
                    }, function (response) {
                        return $q.reject(response.data);
                    });
        },
        queryMSs: function () {
            return $http.get('/ms')
                    .then(function (response) {
                        return response.data;
                    }, function (response) {
                        return $q.reject(response.data);
                    });
        },
        queryFMs: function () {
            return $http.get('/fm')
                    .then(function (response) {
                        return response.data;
                    }, function (response) {
                        return $q.reject(response.data);
                    });
        },
        getURLs: function () {
            return $http.get('/url')
                    .then(function (response) {
                        return response.data;
                    }, function (response) {
                        return $q.reject(response.data);
                    });
        },
        createLU: function (dataObj) {
            return $http.post('/lu', dataObj)
                    .then(function (response) {
                        return response.data;
                    }, function (response) {
                        return $q.reject(response.data);
                    });
        },
        createMS: function (luId, dataObj) {
            return $http.post('/lu/' + luId + '/ms', dataObj)
                    .then(function (response) {
                        return response.data;
                    }, function (response) {
                        return $q.reject(response.data);
                    });
        },
        createFM: function (luId, msId, dataObj) {
            return $http.post('/lu/' + luId + '/ms/' + msId + '/fm', dataObj)
                    .then(function (response) {
                        return response.data;
                    }, function (response) {
                        return $q.reject(response.data);
                    });
        },
        deleteLS: function (queryLSID) {
            return $http.delete('/ls/' + queryLSID)
                    .then(function (response) {
                        return response.data;
                    }, function (response) {
                        return $q.reject(response.data);
                    });
        },
        deleteLU: function (queryLUID) {
            return $http.delete('/lu/' + queryLUID)
                    .then(function (response) {
                        return response.data;
                    }, function (response) {
                        return $q.reject(response.data);
                    });
        },
        deleteMS: function (queryMSID) {
            return $http.delete(address.value + '/ms/' + queryMSID)
                    .then(function (response) {
                        return response.data;
                    }, function (response) {
                        return $q.reject(response.data);
                    });
        },
        deleteFM: function (queryMSID, queryFMID) {
            return $http.delete('/ms/' + queryMSID + '/fm/' + queryFMID)
                    .then(function (response) {
                        return response.data;
                    }, function (response) {
                        return $q.reject(response.data);
                    });
        },
        luMode: function (luId, mode) {
            return $http.put('/lu/' + luId + '?mode=' + mode)
                    .then(function (response) {
                        return response.data;
                    }, function (response) {
                        return $q.reject(response.data);
                    });
        },
        queryLUSensor: function (luId) {
            return $http.get('/lu/' + luId + '/sensor')
                    .then(function (response) {
                        return response.data;
                    }, function (response) {
                        return $q.reject(response.data);
                    });
        },
        queryMSSensor: function (msId) {
            return $http.get('/ms/' + msId + '/sensor')
                    .then(function (response) {
                        return response.data;
                    }, function (response) {
                        return $q.reject(response.data);
                    });
        },
        queryFMSensor: function (fmId) {
            return $http.get('/fm/' + fmId + '/sensor')
                    .then(function (response) {
                        return response.data;
                    }, function (response) {
                        return $q.reject(response.data);
                    });
        },
        queryHMI: function (luId) {
            return $http.get('/lu/' + luId + '/hmi/sensor')
                    .then(function (response) {
                        return response.data;
                    }, function (response) {
                        return $q.reject(response.data);
                    });
        },
        maintenanceControls: function (luId, action) {
            return $http.post('/lu/' + luId + '?action=' + action)
                    .then(function (response) {
                        return response.data;
                    }, function (response) {
                        return $q.reject(response.data);
                    });
        },
        simulateOrStopLS: function (queryID, action) {
            return $http.put('/ls/' + queryID + '?action=' + action)
                    .then(function (response) {
                        return response.data;
                    }, function (response) {
                        return $q.reject(response.data);
                    });
        },
        simulateOrStopLU: function (queryID, action) {
            return $http.put('/lu/' + queryID + '/simulation?action=' + action)
                    .then(function (response) {
                        return response.data;
                    }, function (response) {
                        return $q.reject(response.data);
                    });
        },
        forwardTime: function (luId, iterator, value) {
            return $http.post('/' + luId + '/steps?' + iterator + '=' + value)
                    .then(function (response) {
                        return response.data;
                    }, function (response) {
                        return $q.reject(response.data);
                    });
        }
    };
});

lubricationSystemApp.controller('homeCtrl', ['$scope', 'httpReq', '$window', '$uibModal', '$log', '$filter', function ($scope, httpReq, $window, $uibModal, $log, $filter) {

        $scope.ids = {
            lsId: '',
            luId: '',
            msId: '',
            fmId: '',
            msIds: []
        };

        $scope.switch = 'off';

        $scope.createIds = {
            lsId: '',
            luId: '',
            msId: '',
            fmId: '',
            msIds: []
        };

        $scope.alerts = {
            designLUAlerts: [],
            designMSAlerts: [],
            luPointsAlerts: [],
            assignFMAlerts: [],
            doneAlerts: [],
            designFMAlerts: [],
            createAlerts: [],
            deleteFMAlerts: [],
            addFMAlerts: [],
            stepAlerts: [],
            maintenanceAlerts: []
        };

        $scope.status = {
            isCreateLUEnabled: true,
            isCreateMSEnabled: false,
            isAllocateLubricationPointsEnabled: false,
            isCreateFMEnabled: false,
            isAssignFMEnabled: false,
            isDoneEnabeled: false,
            isSimulateEnabled: false,
            isAddDeleteFlowmeterButton: true,
            isAddDeletePanel: false,
            isAddPanel: false,
            isDeletePanel: false,
            createButtonDisabled: true
        };

        $scope.time = {
            mode: 'minute',
            iteration: '1x'
        };

        $scope.luPanel = {
            luTankCapacity: '',
            luFlow: '',
            luFilterCapacity: ''
        };

        $scope.msPanel = {
            msNo: '',
            msNoArray: []
        };

        $scope.allocPanel = {
            totalFMAlloc: 0
        };

        $scope.fmPanel = {
            fmNoList: [],
            totalFMNo: '',
            fmInMSMaxHolder: '',
            fmInMSNo: '',
            fmInMSNoList: [],
            msIdIncrementer: 0,
            maxFlowHolder: 0.0,
            msId: '',
            nextNoFM: 0
        };

        $scope.initialScreen = {
            slides: [{image: 'img/LubricationSystem.jpg'}, {image: 'img/LubricationUnit.jpg'}, {image: 'img/MeasuringStation.jpg'}, {image: 'img/FlowMeter.jpg'}]
        };

        $scope.queryDetails = {
            lubricationUnits: [],
            measuringStations: [],
            flowMeters: [],
            luSensors: [],
            msSensors: [],
            fmSensors: []
        };

        $scope.simulateSystems = {
            lubricationUnit: [],
            lubricationUnits: [],
            measuringStations: [],
            flowMeters: [],
            msSensors: [],
            selFMSensors: [],
            selMSId: '',
            querySensors: 1,
            controlStatus: '',
            controlAlert: '',
            controlAlertStatus: false,
            controlStyle: {
                'background-color': '#FFFFFF'},
            machineStatus: '',
            machineStyle: {
                'background-color': '#FFFFFF'},
            tableShow: false,
            gaugeMS: ''
        };

        $scope.displaySystem = {
            lubricationUnit: [],
            lubricationUnits: [],
            measuringStations: [],
            flowMeters: []
        };

        dummy = {
            currentFM: [],
            currentMS: []
        };

        $scope.instructionsScreen = {
            basicScreen: true,
            monitoringScreen: false,
            rtuScreen: false,
            operatorScreen: false,
            basicStyle: {'background-color': '#8AC5FF', 'color': 'black'},
            monitorStyle: {},
            rtuStyle: {},
            operatorStyle: {}
        };

        $scope.progressBar = {
            progressBarText: '',
            progressBarValue: '',
            progressBarStatus: false
        };

        $scope.screen = {
            initialScreen: true,
            createScreen: false,
            simulateScreen: true,
            operatorScreen: false,
            monitorSelection: false,
            aboutScreen: false,
            instructionsScreen: false,
            contactScreen: false
        };

        $scope.deteleVar = {
            msId: '',
            fmIds: [],
            fmId: ''
        };

        $scope.addVar = {
            msId: '',
            flow: '',
            maxFlowHolder: 0.0
        };

        $scope.operatorPanel = {
            messages: [],
            oilLedStyle: {
                'margin': '0 auto',
                'width': '24px',
                'height': '24px',
                'background-color': '#A00',
                'border-radius': '50%'
            },
            filterLedStyle: {
                'margin': '0 auto',
                'width': '24px',
                'height': '24px',
                'background-color': '#A00',
                'border-radius': '50%'
            },
            machineLedStyle: {
                'margin': '0 auto',
                'width': '40px',
                'height': '40px',
                'background-color': '#FF0000',
                'border-radius': '50%',
                'box-shadow': 'rgba(0, 0, 0, 0.2) 0 -1px 7px 1px, inset #441313 0 -1px 9px, rgba(255, 0, 0, 0.5) 0 2px 12px'
            },
            redAnimatesBurningStyle: {
                'margin': '0 auto',
                'width': '24px',
                'height': '24px',
                'background-color': '#FF0000',
                'border-radius': '50%',
                'box-shadow': 'rgba(0, 0, 0, 0.2) 0 -1px 7px 1px, inset #441313 0 -1px 9px, rgba(255, 0, 0, 0.5) 0 2px 12px',
                '-webkit-animation': 'blinkRed 0.5s infinite',
                '-moz-animation': 'blinkRed 0.5s infinite',
                '-ms-animation': 'blinkRed 0.5s infinite',
                '-o-animation': 'blinkRed 0.5s infinite',
                'animation': 'blinkRed 0.5s infinite'
            },
            greenBurningStyle: {
                'margin': '0 auto',
                'width': '40px',
                'height': '40px',
                'background-color': '#ABFF00',
                'border-radius': '50%',
                'box-shadow': 'rgba(0, 0, 0, 0.2) 0 -1px 7px 1px, inset #304701 0 -1px 9px, #89FF00 0 2px 12px'
            },
            redBurningStyleMachine: {
                'margin': '0 auto',
                'width': '40px',
                'height': '40px',
                'background-color': '#FF0000',
                'border-radius': '50%',
                'box-shadow': 'rgba(0, 0, 0, 0.2) 0 -1px 7px 1px, inset #441313 0 -1px 9px, rgba(255, 0, 0, 0.5) 0 2px 12px'
            },
            redBurningStyleSensor: {
                'margin': '0 auto',
                'width': '24px',
                'height': '24px',
                'background-color': '#A00',
                'border-radius': '50%'
            },
            queryHMI: 0,
            simTime: '',
            machineOn: false,
            lastMessage: ''
        };

        $scope.fmInEachMSNo = [];

        $scope.fmInEachMS = [];

        $scope.msForEachFM = [];

        $scope.lubricationUnit = {};

        $scope.measuringStationIds = [];

        $scope.luResponse = '';

        $scope.myUrl = '';

        $scope.createFlag = false;

        $scope.dumi = 2;

        //-----------------------------Simulation gauges ----------------------

        $scope.gaugeEnable = false; // enables the limit setting for gauges

        $scope.luLevelGauge = {
            value: 0,
            upperLimit: 9000,
            lowerLimit: 0,
            unit: "(l)",
            precision: 2,
            ranges: [
                {
                    min: 0,
                    max: 2500,
                    color: '#C50200'
                },
                {
                    min: 2500,
                    max: 5000,
                    color: '#FF7700'
                },
                {
                    min: 5000,
                    max: 7000,
                    color: '#FDC702'
                },
                {
                    min: 7000,
                    max: 9000,
                    color: '#8DCA2F'
                }
            ]
        };

        $scope.luFilterGauge = {
            value: 0,
            upperLimit: 1350,
            lowerLimit: 0,
            unit: "(l)",
            precision: 2,
            ranges: [
                {
                    min: 0,
                    max: 250,
                    color: '#C50200'
                },
                {
                    min: 250,
                    max: 500,
                    color: '#FF7700'
                },
                {
                    min: 500,
                    max: 750,
                    color: '#FDC702'
                },
                {
                    min: 750,
                    max: 1350,
                    color: '#8DCA2F'
                }
            ]
        };

        $scope.luInFlowGauge = {
            value: 0,
            upperLimit: 900,
            lowerLimit: 0,
            unit: "(l/min)",
            precision: 2,
            ranges: [
                {
                    min: 0,
                    max: 700,
                    color: '#8DCA2F'
                },
                {
                    min: 700,
                    max: 800,
                    color: '#FF7700'
                },
                {
                    min: 800,
                    max: 900,
                    color: '#FF7700'
                }
            ]
        };

        $scope.luOutFlowGauge = {
            value: 0,
            upperLimit: 900,
            lowerLimit: 0,
            unit: "(l/min)",
            precision: 2,
            ranges: [
                {
                    min: 0,
                    max: 700,
                    color: '#8DCA2F'
                },
                {
                    min: 700,
                    max: 800,
                    color: '#FF7700'
                },
                {
                    min: 800,
                    max: 900,
                    color: '#FF7700'
                }
            ]
        };

        $scope.luTempGauge = {
            value: 0
        };

        $scope.luWaterGauge = {
            value: 0
        };

        $scope.luParticelBox = {
            value: ''
        };

        $scope.msFlowGauge = {
            value: 0,
            upperLimit: 900,
            lowerLimit: 0,
            unit: "(l/min)",
            precision: 2,
            ranges: [
                {
                    min: 0,
                    max: 250,
                    color: '#C50200'
                },
                {
                    min: 250,
                    max: 500,
                    color: '#FF7700'
                },
                {
                    min: 500,
                    max: 700,
                    color: '#FDC702'
                },
                {
                    min: 700,
                    max: 900,
                    color: '#8DCA2F'
                }
            ]
        };

        $scope.msTempGauge = {
            value: 0
        };

        $scope.msPresGauge = {
            value: 0
        };

        $scope.fmFlowGauges = [];

        //----------------------------------------------------------------------

        (function myLoop(index) {
            setTimeout(function () {
                if (index === 1) {
                    $scope.screen.simulateScreen = false;
                    console.log('entered');
                }
                if (--index) {
                    myLoop(index);
                }
            }, 300);
        })($scope.dumi);

        /**
         * Adding an alerts in the HTML
         * 
         * @param {type} alert
         * @param {type} msg
         * @returns {undefined}
         */
        $scope.addAlert = function (alert, msg) {
            alert.push(msg);
        };
        /**
         * Closing the Alerst in HTML
         * 
         * @param {type} alert
         * @returns {undefined}
         */
        $scope.closeAlert = function (alert) {
            alert.splice(0, 1);
        };
        /**
         * Function which sets the status of all the enabled things to false
         * 
         * @returns {undefined}
         */
        $scope.setStatus = function () {
            $scope.status.isCreateLUEnabled = false;
            $scope.status.isCreateMSEnabled = false;
            $scope.status.isAllocateLubricationPointsEnabled = false;
            $scope.status.isCreateFMEnabled = false;
            $scope.status.isAssignFMEnabled = false;
            $scope.status.isDoneEnabeled = false;
            $scope.status.isSimulateEnabled = false;
        };
        /**
         * Function to Query Simulator for the available of components
         * 
         * @returns {undefined}
         */
        $scope.queryElements = function () {
            httpReq.queryLUs()
                    .then(function (data) {
                        $scope.queryDetails.lubricationUnits = data;
                        $scope.makeSystem();
                    }, function (error) {
                        console.log(error);
                    });
            httpReq.queryMSs()
                    .then(function (data) {
                        $scope.queryDetails.measuringStations = data;
                        $scope.makeSystem();
                    }, function (error) {
                        console.log(error);
                    });
            httpReq.queryFMs()
                    .then(function (data) {
                        $scope.queryDetails.flowMeters = data;
                        $scope.makeSystem();
                    }, function (error) {
                        console.log(error);
                    });
        };
        /**
         * Function that converts the individual queried components from the simulator into a complete system in the Client App
         * 
         * @returns {undefined}
         */
        $scope.makeSystem = function () {
            $scope.simulateSystems.lubricationUnit = [];
            $scope.simulateSystems.lubricationUnits = [];
            $scope.simulateSystems.measuringStations = [];
            $scope.simulateSystems.flowMeters = [];
            $scope.displaySystem.lubricationUnit = [];
            $scope.displaySystem.lubricationUnits = [];
            $scope.displaySystem.measuringStations = [];
            $scope.displaySystem.flowMeters = [];
            for (indexMS_1 = 0, lenMS_1 = $scope.queryDetails.measuringStations.length; indexMS_1 < lenMS_1; ++indexMS_1) {
                dummy.currentFM.length = 0;
                dummy.currentFM = Object.keys($scope.queryDetails.measuringStations[indexMS_1].flowMeters);
                for (indexFM = 0, lenFM = dummy.currentFM.length; indexFM < lenFM; ++indexFM) {
                    for (indexFM_1 = 0, lenFM_1 = $scope.queryDetails.flowMeters.length; indexFM_1 < lenFM_1; ++indexFM_1) {
                        if (String($scope.queryDetails.flowMeters[indexFM_1].id) === String(dummy.currentFM[indexFM])) {
                            $scope.queryDetails.measuringStations[indexMS_1].flowMeters[$scope.queryDetails.flowMeters[indexFM_1].id] = $scope.queryDetails.flowMeters[indexFM_1];
                        }
                    }
                }
            }

            for (indexLU_1 = 0, lenLU_1 = $scope.queryDetails.lubricationUnits.length; indexLU_1 < lenLU_1; ++indexLU_1) {
                dummy.currentMS.length = 0;
                dummy.currentMS = Object.keys($scope.queryDetails.lubricationUnits[indexLU_1].measuringStations);
                for (indexMS = 0, lenMS = dummy.currentMS.length; indexMS < lenMS; ++indexMS) {
                    for (indexMS_1 = 0, lenMS_1 = $scope.queryDetails.measuringStations.length; indexMS_1 < lenMS_1; ++indexMS_1) {
                        dummy.currentFM.length = 0;
                        dummy.currentFM = Object.keys($scope.queryDetails.measuringStations[indexMS_1].flowMeters);
                        for (indexFM = 0, lenFM = dummy.currentFM.length; indexFM < lenFM; ++indexFM) {
                            for (indexFM_1 = 0, lenFM_1 = $scope.queryDetails.flowMeters.length; indexFM_1 < lenFM_1; ++indexFM_1) {
                                if (String($scope.queryDetails.flowMeters[indexFM_1].id) === String(dummy.currentFM[indexFM])) {
                                    $scope.queryDetails.measuringStations[indexMS_1].flowMeters[$scope.queryDetails.flowMeters[indexFM_1].id] = $scope.queryDetails.flowMeters[indexFM_1];
                                }
                            }
                        }
                        if (String($scope.queryDetails.measuringStations[indexMS_1].id) === String(dummy.currentMS[indexMS])) {
                            $scope.queryDetails.lubricationUnits[indexLU_1].measuringStations[$scope.queryDetails.measuringStations[indexMS_1].id] = $scope.queryDetails.measuringStations[indexMS_1];
                        }
                    }
                }
            }


            tempFlagLU = 0;
            for (indexLU = 0, lenLU = $scope.queryDetails.lubricationUnits.length; indexLU < lenLU; ++indexLU) {
                if ($scope.queryDetails.lubricationUnits[indexLU].id === $scope.ids.luId) {
                    $scope.simulateSystems.lubricationUnit = $scope.queryDetails.lubricationUnits[indexLU];
                    $scope.displaySystem.lubricationUnit = $scope.queryDetails.lubricationUnits[indexLU];
                    tempFlagLU = 1;
                }
            }
            ;
            if (tempFlagLU !== 1 && ($scope.queryDetails.lubricationUnits.length !== 0)) {
                $scope.simulateSystems.lubricationUnit = $scope.queryDetails.lubricationUnits[0];
                $scope.displaySystem.lubricationUnit = $scope.queryDetails.lubricationUnits[0];
                $scope.ids.luId = $scope.simulateSystems.lubricationUnit.id;
            }
            if ($scope.simulateSystems.lubricationUnit.length !== 0 || $scope.simulateSystems.lubricationUnit !== '' || $scope.simulateSystems.lubricationUnit !== undefined || $scope.simulateSystems.lubricationUnit !== null) {
                for (indexMS = 0, lenMS = $scope.queryDetails.measuringStations.length; indexMS < lenMS; ++indexMS) {
                    dummy.currentMS.length = 0;
                    if ($scope.simulateSystems.lubricationUnit.measuringStations !== undefined) {
                        dummy.currentMS = Object.keys($scope.simulateSystems.lubricationUnit.measuringStations);
                        for (indexMS_1 = 0, lenMS_1 = dummy.currentMS.length; indexMS_1 < lenMS_1; ++indexMS_1) {
                            if (dummy.currentMS[indexMS_1] === $scope.queryDetails.measuringStations[indexMS].id) {
                                $scope.simulateSystems.measuringStations.push($scope.queryDetails.measuringStations[indexMS]);
                            }
                        }
                    }
                }
                ;
                for (indexFM = 0, lenFM = $scope.queryDetails.flowMeters.length; indexFM < lenFM; ++indexFM) {
                    for (indexMS = 0, lenMS = $scope.simulateSystems.measuringStations.length; indexMS < lenMS; ++indexMS) {
                        dummy.currentFM.length = 0;
                        dummy.currentFM = Object.keys($scope.simulateSystems.measuringStations[indexMS].flowMeters);
                        for (indexFM_1 = 0, lenFM_1 = dummy.currentFM.length; indexFM_1 < lenFM_1; ++indexFM_1) {
                            if (dummy.currentFM[indexFM_1] === $scope.queryDetails.flowMeters[indexFM].id) {
                                $scope.simulateSystems.flowMeters.push($scope.queryDetails.flowMeters[indexFM]);
                            }
                        }
                    }
                }
                ;
            }
            if (($scope.gaugeEnable) && ($scope.displaySystem.lubricationUnit.length !== 0 || $scope.displaySystem.lubricationUnit !== '' || $scope.displaySystem.lubricationUnit !== undefined || $scope.displaySystem.lubricationUnit !== null)) {
                $scope.changeluLevelGauge($scope.displaySystem.lubricationUnit.tankCapacity);
                $scope.changeluFilterGauge($scope.displaySystem.lubricationUnit.filterCapacity);
                $scope.changeluFlowGauge($scope.displaySystem.lubricationUnit.maxOilFlow);
            }
        };
        for (index = 1, len = 91; index < len; ++index) {
            $scope.fmPanel.fmNoList.push(index);
        }
        ;

        $scope.emptyAll = function () {
            $scope.ids.lsId = '';
            $scope.ids.luId = '';
            $scope.ids.msId = '';
            $scope.ids.fmId = '';
            $scope.ids.msIds = [];
            $scope.luPanel.luTankCapacity = '';
            $scope.luPanel.luFlow = '';
            $scope.luPanel.luFilterCapacity = '';
            $scope.msPanel.msNo = '';
            $scope.msPanel.msNoArray = [];
            $scope.fmPanel.totalFMNo = '';
            $scope.fmPanel.fmInMSMaxHolder = '';
            $scope.fmPanel.fmInMSNo = '';
            $scope.fmPanel.fmInMSNoList = [];
            $scope.fmPanel.msIdIncrementer = 0;
            $scope.fmPanel.maxFlowHolder = 0.0;
            $scope.fmPanel.msId = '';
            $scope.fmPanel.nextNoFM = 0;
            $scope.queryDetails.lubricationUnits = [];
            $scope.queryDetails.measuringStations = [];
            $scope.queryDetails.flowMeters = [];
            $scope.queryDetails.luSensors = [];
            $scope.queryDetails.msSensors = [];
            $scope.queryDetails.fmSensors = [];
            $scope.displaySystem.lubricationUnit = [];
            $scope.displaySystem.lubricationUnits = [];
            $scope.displaySystem.measuringStations = [];
            $scope.displaySystem.flowMeters = [];
            $scope.fmInEachMSNo = [];
            $scope.fmInEachMS = [];
            $scope.msForEachFM = [];
            $scope.lubricationUnit = {};
            $scope.measuringStationIds = [];
            $scope.luResponse = '';
            $scope.myUrl = '';
            $scope.allocPanel.totalFMAlloc = 0;
            $scope.simulateSystems.controlStatus = '';
            $scope.simulateSystems.controlAlert = '';
            $scope.simulateSystems.controlAlertStatus = false;
            $scope.simulateSystems.controlStyle = {
                'background-color': '#FFFFFF'};
            $scope.simulateSystems.tableShow = false;
            $scope.switch = 'off';
            $scope.status.isAddDeleteFlowmeterButton = true;
            $scope.status.isAddDeletePanel = false;
            $scope.status.isDeletePanel = false;
            $scope.status.isAddPanel = false;
        };

        $scope.setInstructionsScreen = function () {
            $scope.instructionsScreen.basicScreen = false;
            $scope.instructionsScreen.monitoringScreen = false;
            $scope.instructionsScreen.rtuScreen = false;
            $scope.instructionsScreen.operatorScreen = false;
            $scope.instructionsScreen.basicStyle = {};
            $scope.instructionsScreen.rtuStyle = {};
            $scope.instructionsScreen.monitorStyle = {};
            $scope.instructionsScreen.operatorStyle = {};
        }

        //------------------------------Header Panel--------------------------------

        $scope.setScreenFalse = function () {
            $scope.screen.createScreen = false;
            $scope.screen.simulateScreen = false;
            $scope.screen.monitorSelection = false;
            $scope.screen.operatorScreen = false;
            $scope.screen.initialScreen = false;
            $scope.screen.aboutScreen = false;
            $scope.screen.instructionsScreen = false;
            $scope.screen.contactScreen = false;
        };

        $scope.homePanelClick = function () {
            if (!$scope.createFlag) {
                $scope.createIds.lsId = $scope.ids.lsId;
            }
            $scope.emptyAll();
            $scope.setStatus();
            $scope.setScreenFalse();
            $scope.gaugeEnable = false;
            $scope.screen.initialScreen = true;
            $scope.simulateSystems.querySensors = 0;
        };

        $scope.aboutClick = function () {
            if (!$scope.createFlag) {
                $scope.createIds.lsId = $scope.ids.lsId;
            }
            $scope.emptyAll();
            $scope.setStatus();
            $scope.setScreenFalse();
            $scope.gaugeEnable = false;
            $scope.screen.aboutScreen = true;
            $scope.simulateSystems.querySensors = 0;
        };

        $scope.instructionsClick = function () {
            if (!$scope.createFlag) {
                $scope.createIds.lsId = $scope.ids.lsId;
            }
            $scope.emptyAll();
            $scope.setStatus();
            $scope.setScreenFalse();
            $scope.setInstructionsScreen();
            $scope.instructionsScreen.basicScreen = true;
            $scope.gaugeEnable = false;
            $scope.screen.instructionsScreen = true;
            $scope.simulateSystems.querySensors = 0;
            $scope.instructionsScreen.basicStyle = {'background-color': '#8AC5FF', 'color': 'black'};
        };

        $scope.contactsClick = function () {
            if (!$scope.createFlag) {
                $scope.createIds.lsId = $scope.ids.lsId;
            }
            $scope.emptyAll();
            $scope.setStatus();
            $scope.setScreenFalse();
            $scope.gaugeEnable = false;
            $scope.screen.contactScreen = true;
            $scope.simulateSystems.querySensors = 0;
        };

        //-------------------------------Information Panel--------------------------

        $scope.basicInfoClick = function () {
            $scope.setInstructionsScreen();
            $scope.instructionsScreen.basicScreen = true;
            $scope.instructionsScreen.basicStyle = {'background-color': '#8AC5FF', 'color': 'black'};
        };

        $scope.monitoringInfoClick = function () {
            $scope.setInstructionsScreen();
            $scope.instructionsScreen.monitoringScreen = true;
            $scope.instructionsScreen.monitorStyle = {'background-color': '#8AC5FF'};
        };

        $scope.RTUInfoClick = function () {
            $scope.setInstructionsScreen();
            $scope.instructionsScreen.rtuScreen = true;
            $scope.instructionsScreen.rtuStyle = {'background-color': '#8AC5FF', 'color': 'black'};
        };

        $scope.operatorInfoClick = function () {
            $scope.setInstructionsScreen();
            $scope.instructionsScreen.operatorScreen = true;
            $scope.instructionsScreen.operatorStyle = {'background-color': '#8AC5FF', 'color': 'black'};
        };

        //------------------------------First Screen--------------------------------
        /*
         * Create new System
         * 
         * @returns {undefined}
         */
        $scope.createNew = function () {
//            var dataObj = {
//            };
//            if ($scope.createIds.lsId === '') {
//                httpReq.createLS(dataObj)
//                        .then(function (data) {
//                            $scope.ids.lsId = data.id;
//                            if ($scope.ids.lsId !== '') {
//                                $scope.createIds.lsId = $scope.ids.lsId;
//                                $scope.setStatus();
//                                $scope.setScreenFalse();
//                                $scope.status.isCreateLUEnabled = true;
//                                $scope.screen.createScreen = true;
//                                $scope.status.createButtonDisabled = false;
//                            }
//                        }, function (error) {
//                            console.log(error);
//                        });
//            } else {
//                $scope.ids.lsId = $scope.createIds.lsId;
//                $scope.setStatus();
//                $scope.setScreenFalse();
//                $scope.status.isCreateLUEnabled = true;
//                $scope.screen.createScreen = true;
//                $scope.status.createButtonDisabled = false;
//            }
            $scope.setStatus();
            $scope.setScreenFalse();
            $scope.status.isCreateLUEnabled = true;
            $scope.screen.createScreen = true;
            $scope.status.createButtonDisabled = false;
        };

        /*
         * Monitor Existing System
         * 
         * @returns {undefined}
         */
        $scope.monitorSystem = function () {
            $scope.queryElements();
            $scope.setStatus();
            $scope.setScreenFalse();
            $scope.screen.monitorSelection = true;
        };

        //------------------------------Moniror Screen--------------------------------
        /*
         * Monitors the System as per the selection
         * 
         * @param {type} id
         * @returns {undefined}
         */
        $scope.luMonitor = function (id) {
            $scope.ids.luId = id;
            $scope.queryElements();
            for (indexLU_1 = 0, lenLU_1 = $scope.queryDetails.lubricationUnits.length; indexLU_1 < lenLU_1; ++indexLU_1) {
                if ($scope.queryDetails.lubricationUnits[indexLU_1].id === id) {
                    $scope.ids.lsId = $scope.queryDetails.lubricationUnits[indexLU_1].parentId;
                }
            }
            $scope.queryElements();
            $scope.screen.simulateScreen = true;
            $scope.screen.monitorSelection = false;
            $scope.simulateSystems.controlAlertStatus = false;
            $scope.simulateSystems.controlAlert = '';
            $scope.simulateSystems.querySensors = 1;
            $scope.getIP();
            $scope.querySensors();
            $scope.gaugeEnable = true;
        };

        $scope.monitorBack = function () {
            $scope.emptyAll();
            $scope.setStatus();
            $scope.setScreenFalse();
            $scope.gaugeEnable = false;
            $scope.screen.initialScreen = true;
            $scope.simulateSystems.querySensors = 0;
        };

        //------------------------------Create Screen--------------------------------

        // create LU pane
        /**
         * function for Next button in the LU creation
         * 
         * @returns {undefined}
         */
        $scope.luNext = function () {
            if (($scope.luPanel.luTankCapacity === '') || ($scope.luPanel.luTankCapacity === null)) {
                $scope.luPanel.luTankCapacity = 9000.0;
            }
            if (($scope.luPanel.luFlow === '') || ($scope.luPanel.luFlow === null)) {
                $scope.luPanel.luFlow = 900.0;
            }
            if (($scope.luPanel.luFilterCapacity === '') || ($scope.luPanel.luFilterCapacity === null)) {
                $scope.luPanel.luFilterCapacity = 1350.0;
            }
            if (($scope.luPanel.luTankCapacity > $scope.luPanel.luFlow) || (($scope.luPanel.luFlow === '') && (($scope.luPanel.luTankCapacity === '') || ($scope.luPanel.luTankCapacity > 900)))) {
                if (($scope.luPanel.luFilterCapacity === 0.0) || ($scope.luPanel.luFilterCapacity === 0.0) || ($scope.luPanel.luFilterCapacity === 0.0)) {
                    $scope.closeAlert($scope.alerts.designLUAlerts);
                    $scope.addAlert($scope.alerts.designLUAlerts, {type: 'danger', msg: 'Inputs shall not be 0'});
                } else {
                    $scope.setStatus();
                    $scope.closeAlert($scope.alerts.designLUAlerts);
                    $scope.status.isCreateMSEnabled = true;
                }
            } else if (($scope.luPanel.luFlow === '') && ($scope.luPanel.luTankCapacity > 900)) {
                $scope.closeAlert($scope.alerts.designLUAlerts);
                $scope.addAlert($scope.alerts.designLUAlerts, {type: 'warning', msg: 'oil flow must be 10% of Tank Capacity'});
            } else {
                $scope.closeAlert($scope.alerts.designLUAlerts);
                $scope.addAlert($scope.alerts.designLUAlerts, {type: 'danger', msg: 'Maximum oil flow must be lower than Tank Capacity'});
            }
        };

        $scope.createLUBack = function () {
//            if ($scope.ids.lsId !== '') {
//                httpReq.deleteLS($scope.ids.lsId)
//                        .then(function (data) {
            $scope.emptyAll();
            $scope.setStatus();
            $scope.setScreenFalse();
            $scope.gaugeEnable = false;
            $scope.screen.initialScreen = true;
            $scope.simulateSystems.querySensors = 0;
//                        }, function (error) {
//                            console.log(error);
//
//                        });
//            }
        };


        //Create MS pane
        /*
         * Back button function takes to the create LU pane
         * 
         * @returns {undefined}
         */
        $scope.msBack = function () {
            $scope.setStatus();
            $scope.status.isCreateLUEnabled = true;
        };

        /**
         * function for Next button in the MS creation
         * 
         * @returns {undefined}
         */
        $scope.msNext = function () {
            if ($scope.msPanel.msNo !== '') {
                if ($scope.fmInEachMSNo.length !== $scope.msPanel.msNo) {
                    if ($scope.fmInEachMSNo.length === 0) {
                        for (index = 0, len = $scope.msPanel.msNo; index < len; ++index) {
                            $scope.fmInEachMSNo.push({msId: 'MeasuringStation_' + index, value: ''});
                        }
                    } else if ($scope.fmInEachMSNo.length < $scope.msPanel.msNo) {
                        tempLen = 0;
                        tempLen = $scope.msPanel.msNo - $scope.fmInEachMSNo.length;
                        for (index = 0, len = tempLen; index < len; ++index) {
                            $scope.fmInEachMSNo.push({msId: 'MeasuringStation_' + $scope.fmInEachMSNo.length, value: ''});
                        }
                    } else if ($scope.fmInEachMSNo.length > $scope.msPanel.msNo) {
                        tempLen = 0;
                        tempLen = $scope.fmInEachMSNo.length - $scope.msPanel.msNo;
                        for (index = 0, len = tempLen; index < len; ++index) {
                            $scope.fmInEachMSNo.pop();
                        }
                    }
                }
                $scope.setStatus();
                $scope.status.isAllocateLubricationPointsEnabled = true;
                $scope.closeAlert($scope.alerts.designMSAlerts);
            } else {
                $scope.closeAlert($scope.alerts.designMSAlerts);
                $scope.addAlert($scope.alerts.designMSAlerts, {type: 'danger', msg: 'There must be atleast one Measuring Station'});
            }
        };

        //Create Lubrication points pane
        /*
         * Back button function takes to the create MS pane
         * 
         * @returns {undefined}
         */
        $scope.luPointsBack = function () {
            $scope.setStatus();
            $scope.status.isCreateMSEnabled = true;
        };

        /**
         * function for Next button in no of lubrication points determination
         * 
         * @returns {undefined}
         */
        $scope.luPointsNext = function () {
            tempSumAssignedFM = 0;
            for (index = 0, len = $scope.fmInEachMSNo.length; index < len; ++index) {
                tempSumAssignedFM += $scope.fmInEachMSNo[index].value;
            }
            if (($scope.fmPanel.totalFMNo === '') || (tempSumAssignedFM < $scope.fmPanel.totalFMNo) || (tempSumAssignedFM > $scope.fmPanel.totalFMNo)) {
                $scope.closeAlert($scope.alerts.assignFMAlerts);
                $scope.addAlert($scope.alerts.assignFMAlerts, {type: 'danger', msg: 'lubrication points no: [' + $scope.fmPanel.totalFMNo + '] and Flow Meter no: [' + tempSumAssignedFM + '] does not match'});
            } else {
                $scope.closeAlert($scope.alerts.assignFMAlerts);
                $scope.setStatus();
                $scope.status.isCreateFMEnabled = true;
                if ($scope.fmInEachMS.length === 0) {
                    for (index = 0, len = $scope.fmInEachMSNo.length; index < len; ++index) {
                        tempArray = [];
                        for (index_value = 0, len_value = $scope.fmInEachMSNo[index].value; index_value < len_value; ++index_value) {
                            tempArray.push({fmId: 'FlowMeter_' + index_value, flow: ''});
                        }
                        $scope.fmInEachMS.push({msId: $scope.fmInEachMSNo[index].msId, fm: tempArray});
                    }
                } else if ($scope.fmInEachMS.length < parseInt($scope.msPanel.msNo)) {
                    tempLength = $scope.msPanel.msNo - $scope.fmInEachMS.length;
                    for (index = 0, len = $scope.fmInEachMSNo.length; index < len; ++index) {
                        for (index_1 = 0, len_1 = $scope.fmInEachMS.length; index_1 < len_1; ++index_1) {
                            if ($scope.fmInEachMSNo[index].msId === $scope.fmInEachMS[index_1].msId) {
                                if ($scope.fmInEachMSNo[index].value > $scope.fmInEachMS[index_1].fm.length) {
                                    tempLen = $scope.fmInEachMSNo[index].value - $scope.fmInEachMS[index_1].fm.length;
                                    for (index_2 = 0, len_2 = tempLen; index_2 < len_2; ++index_2) {
                                        $scope.fmInEachMS[index_1].fm.push({fmId: 'FlowMeter_' + $scope.fmInEachMS[index_1].fm.length, flow: ''});
                                    }
                                } else if ($scope.fmInEachMSNo[index].value < $scope.fmInEachMS[index_1].fm.length) {
                                    tempLen = $scope.fmInEachMS[index_1].fm.length - $scope.fmInEachMSNo[index].value;
                                    for (index_2 = 0, len_2 = tempLen; index_2 < len_2; ++index_2) {
                                        $scope.fmInEachMS[index_1].fm.pop();
                                    }
                                }
                            }
                        }
                    }
                    tempArray = [];
                    for (index = 0, len = tempLength; index < len; ++index) {
                        for (index_value = 0, len_value = $scope.fmInEachMSNo[$scope.fmInEachMS.length].value; index_value < len_value; ++index_value) {
                            tempArray.push({fmId: 'FlowMeter_' + index_value, flow: ''});
                        }
                        $scope.fmInEachMS.push({msId: $scope.fmInEachMSNo[$scope.fmInEachMS.length].msId, fm: tempArray});
                    }
                } else if ($scope.fmInEachMS.length > parseInt($scope.msPanel.msNo)) {
                    tempLength = $scope.fmInEachMS.length - $scope.msPanel.msNo;
                    for (index = 0, len = $scope.fmInEachMSNo.length; index < len; ++index) {
                        for (index_1 = 0, len_1 = $scope.fmInEachMS.length; index_1 < len_1; ++index_1) {
                            if ($scope.fmInEachMSNo[index].msId === $scope.fmInEachMS[index_1].msId) {
                                if ($scope.fmInEachMSNo[index].value > $scope.fmInEachMS[index_1].fm.length) {
                                    tempLen = $scope.fmInEachMSNo[index].value - $scope.fmInEachMS[index_1].fm.length;
                                    for (index_2 = 0, len_2 = tempLen; index_2 < len_2; ++index_2) {
                                        $scope.fmInEachMS[index_1].fm.push({fmId: 'FlowMeter_' + $scope.fmInEachMS[index_1].fm.length, flow: ''});
                                    }
                                } else if ($scope.fmInEachMSNo[index].value < $scope.fmInEachMS[index_1].fm.length) {
                                    tempLen = $scope.fmInEachMS[index_1].fm.length - $scope.fmInEachMSNo[index].value;
                                    for (index_2 = 0, len_2 = tempLen; index_2 < len_2; ++index_2) {
                                        $scope.fmInEachMS[index_1].fm.pop();
                                    }
                                }
                            }
                        }
                    }
                    for (index = 0, len = tempLength; index < len; ++index) {
                        $scope.fmInEachMS.pop();
                    }
                } else if ($scope.fmInEachMS.length === parseInt($scope.msPanel.msNo)) {
                    for (index = 0, len = $scope.fmInEachMSNo.length; index < len; ++index) {
                        for (index_1 = 0, len_1 = $scope.fmInEachMS.length; index_1 < len_1; ++index_1) {
                            if ($scope.fmInEachMSNo[index].msId === $scope.fmInEachMS[index_1].msId) {
                                if ($scope.fmInEachMSNo[index].value > $scope.fmInEachMS[index_1].fm.length) {
                                    tempLen = $scope.fmInEachMSNo[index].value - $scope.fmInEachMS[index_1].fm.length;
                                    for (index_2 = 0, len_2 = tempLen; index_2 < len_2; ++index_2) {
                                        $scope.fmInEachMS[index_1].fm.push({fmId: 'FlowMeter_' + $scope.fmInEachMS[index_1].fm.length, flow: ''});
                                    }
                                } else if ($scope.fmInEachMSNo[index].value < $scope.fmInEachMS[index_1].fm.length) {
                                    tempLen = $scope.fmInEachMS[index_1].fm.length - $scope.fmInEachMSNo[index].value;
                                    for (index_2 = 0, len_2 = tempLen; index_2 < len_2; ++index_2) {
                                        $scope.fmInEachMS[index_1].fm.pop();
                                    }
                                }
                            }
                        }
                    }
                }
            }
            $scope.fmPanel.maxFlowHolder = Math.round(((parseFloat($scope.luPanel.luFlow) * 0.9) / 90) * 100) / 100;

        };

        $scope.individualFlowMeters = function () {
            $scope.allocPanel.totalFMAlloc = 0;
            tempFminMS = parseInt($scope.fmPanel.totalFMNo / $scope.msPanel.msNo);
            tempFminLast = ($scope.fmPanel.totalFMNo - (tempFminMS * $scope.msPanel.msNo));
            for (index = 0, len = $scope.fmInEachMSNo.length; index < len; ++index) {
                $scope.fmInEachMSNo[index].value = tempFminMS;
                if (index === len - 1) {
                    $scope.fmInEachMSNo[index].value += tempFminLast;
                }
                $scope.allocPanel.totalFMAlloc += $scope.fmInEachMSNo[index].value;
            }
        };

        $scope.totalAllocated = function () {
            $scope.allocPanel.totalFMAlloc = 0;
            for (index = 0, len = $scope.fmInEachMSNo.length; index < len; ++index) {
                $scope.allocPanel.totalFMAlloc += $scope.fmInEachMSNo[index].value;
            }
        };

        //Create FM pane
        /*
         * Back button function takes to the create FM pane
         * 
         * @returns {undefined}
         */
        $scope.createFMBack = function () {
            $scope.setStatus();
            $scope.status.isAllocateLubricationPointsEnabled = true;
        };

        $scope.createFMNext = function () {
            tempFlow = 0.0;
            tempFlowComp = 0.0;
            emptyFlowCnt = 0;
            for (index = 0, len = $scope.fmInEachMS.length; index < len; ++index) {
                for (index_1 = 0, len_1 = $scope.fmInEachMS[index].fm.length; index_1 < len_1; ++index_1) {
                    if (($scope.fmInEachMS[index].fm[index_1].flow !== '') && ($scope.fmInEachMS[index].fm[index_1].flow !== null)) {
                        tempFlow += parseFloat($scope.fmInEachMS[index].fm[index_1].flow);
                    } else {
                        emptyFlowCnt += 1;
                    }
                }
            }
            tempFlowComp = tempFlow + (emptyFlowCnt * $scope.fmPanel.maxFlowHolder);
            if (tempFlowComp < (parseFloat($scope.luPanel.luFlow))) {
                $scope.closeAlert($scope.alerts.designFMAlerts);
                for (index = 0, len = $scope.fmInEachMS.length; index < len; ++index) {
                    for (index_1 = 0, len_1 = $scope.fmInEachMS[index].fm.length; index_1 < len_1; ++index_1) {
                        if (($scope.fmInEachMS[index].fm[index_1].flow === '') || ($scope.fmInEachMS[index].fm[index_1].flow === null)) {
                            $scope.fmInEachMS[index].fm[index_1].flow = $scope.fmPanel.maxFlowHolder;
                        }
                    }
                }
                $scope.setStatus();
                $scope.status.isDoneEnabeled = true;
            } else {
                $scope.closeAlert($scope.alerts.designFMAlerts);
                $scope.addAlert($scope.alerts.designFMAlerts, {type: 'danger', msg: 'total flow of flow meter : [' + tempFlow + '] is greater than lubrication Unit Flow : [' + $scope.luPanel.luFlow + ']'});
            }
        };

        $scope.doneBack = function () {
            $scope.setStatus();
            $scope.status.isCreateFMEnabled = true;
            $scope.progressBar.progressBarStatus = false;
            $scope.progressBar.progressBarText = "";
        };

        $scope.create = function () {
            $scope.status.createButtonDisabled = true;
            $scope.progressBar.progressBarStatus = true;
            $scope.progressBar.progressBarValue = 0;
            $scope.progressBar.progressBarText = "InProgress";
            $scope.getIP();
            $scope.createLU();
            $scope.msForEachFM.length = 0;
        };

        $scope.createLU = function () {
            var dataObj = {
                'tankCapacity': $scope.luPanel.luTankCapacity,
                'filterCapacity': $scope.luPanel.luFilterCapacity,
                'maxOilFlow': $scope.luPanel.luFlow
            };
//            httpReq.createLU($scope.ids.lsId,dataObj)
            httpReq.createLU(dataObj)
                    .then(function (data) {
                        $scope.ids.luId = data.id;
                        if ($scope.ids.luId !== '') {
                            $scope.createMS();
                            $scope.progressBar.progressBarValue = 10;
                        }
                    }, function (error) {
                        $scope.closeAlert($scope.alerts.createAlerts);
                        $scope.addAlert($scope.alerts.createAlerts, {type: 'danger', msg: error.response});
                        console.log(error);
                    });
        };

        $scope.createMS = function () {
            (function myLoop(inde) {
                setTimeout(function () {
                    var dataObj = {
                    };
                    httpReq.createMS($scope.ids.luId, dataObj)
                            .then(function (data) {
                                $scope.ids.msId = data.id;
                                for (index = 0, len = $scope.fmInEachMS.length; index < len; ++index) {
                                    if (($scope.fmInEachMS[index].msId.substring($scope.fmInEachMS[index].msId.indexOf("_") + 1)) === ($scope.ids.msId.substring($scope.ids.msId.lastIndexOf("_") + 1))) {
                                        $scope.fmInEachMS[index].msId = data.id;
                                        for (index_1 = 0, len_1 = $scope.fmInEachMS[index].fm.length; index_1 < len_1; ++index_1) {
                                            $scope.msForEachFM.push({msId: data.id, flow: $scope.fmInEachMS[index].fm[index_1].flow});
                                        }
                                    }
                                    $scope.progressBar.progressBarValue += 4;
                                }
                                if (inde === 0) {
                                    $scope.createFM();
                                    $scope.progressBar.progressBarValue = 50;
                                }
                            }, function (error) {
                                $scope.addAlert($scope.alerts.createAlerts, {type: 'danger', msg: error.response});
                                console.log(error);
                            });
                    if (--inde) {
                        myLoop(inde);
                    }
                }, 50);
            })($scope.msPanel.msNo);
        };

        $scope.createFM = function () {
            (function myLoop(index) {
                setTimeout(function () {
                    var dataObj = {
                        'nomFlow': $scope.msForEachFM[index - 1].flow
                    };
                    httpReq.createFM($scope.ids.luId, $scope.msForEachFM[index - 1].msId, dataObj)
                            .then(function (data) {
                                console.log(data.id);
                                if ($scope.progressBar.progressBarValue !== 90) {
                                    $scope.progressBar.progressBarValue += 0.5;
                                }
                                if (index === 0) {
                                    $scope.progressBar.progressBarValue = 100;
                                    $scope.progressBar.progressBarText = "Done";
                                    $scope.Simulate();
                                }
                            }, function (error) {
                                $scope.addAlert($scope.alerts.createAlerts, {type: 'danger', msg: error.response});
                                console.log(error);
                            });
                    if (--index) {
                        myLoop(index);
                    }
                }, 700);
            })($scope.msForEachFM.length);
        };

        $scope.Simulate = function () {
            $scope.createFlag = true;
            httpReq.simulateOrStopLU($scope.ids.luId, "simulate")
                    .then(function (data) {
                        $scope.progressBar.progressBarStatus = false;
                        $scope.progressBar.progressBarValue = 0;
                        $scope.progressBar.progressBarText = "";
                        $scope.setScreenFalse();
                        $scope.screen.simulateScreen = true;
                        $scope.queryElements();
                        $scope.simulateSystems.querySensors = 1;
                        $scope.simulateSystems.controlAlertStatus = false;
                        $scope.simulateSystems.controlAlert = '';
                        $scope.querySensors();
                        $scope.gaugeEnable = true;
                        console.log('Done');
                    }, function (error) {
                        console.log(error);
                    });
        };

        //------------------------------Simulate Screen--------------------------------

        $scope.addDelete = function () {
            httpReq.simulateOrStopLU($scope.ids.luId, "pause")
                    .then(function (data) {
                        $scope.status.isAddDeletePanel = true;
                        $scope.status.isAddDeleteFlowmeterButton = false;
                        $scope.queryElements();
                        $scope.simulateSystems.querySensors = 0;
                    }, function (error) {
                        console.log(error);
                    });
        };

        $scope.addFMPanel = function () {
            $scope.status.isAddDeletePanel = false;
            $scope.status.isDeletePanel = false;
            $scope.status.isAddPanel = true;
            $scope.addVar.msId = '';
            $scope.addVar.flow = '';
            $scope.addVar.maxFlowHolder = 0.0;
        };

        $scope.deleteFMPanel = function () {
            $scope.status.isAddDeletePanel = false;
            $scope.status.isAddPanel = false;
            $scope.status.isDeletePanel = true;
            $scope.deleteVar.msId = '';
            $scope.deteleVar.fmId = '';
        };

        $scope.doneAddDelete = function () {
            httpReq.simulateOrStopLU($scope.ids.luId, "simulate")
                    .then(function (data) {
                        $scope.closeAlert($scope.alerts.deleteFMAlerts);
                        $scope.status.isAddDeletePanel = false;
                        $scope.status.isAddPanel = false;
                        $scope.status.isDeletePanel = false;
                        $scope.status.isAddDeleteFlowmeterButton = true;
                        $scope.queryElements();
                        $scope.simulateSystems.querySensors = 1;
                        $scope.querySensors();
                    }, function (error) {
                        console.log(error);
                    });
        };

        $scope.fmGenerate = function () {
            $scope.deteleVar.fmIds.length = 0;
            for (indexMS_1 = 0, lenMS_1 = $scope.simulateSystems.measuringStations.length; indexMS_1 < lenMS_1; ++indexMS_1) {
                if ($scope.deteleVar.msId === $scope.simulateSystems.measuringStations[indexMS_1].id) {
                    $scope.deteleVar.fmIds = Object.keys($scope.simulateSystems.measuringStations[indexMS_1].flowMeters);
                }
            }
        };

        $scope.fmFlowGenerate = function () {
            $scope.addVar.maxFlowHolder = 0.0;
            for (indexMS_1 = 0, lenMS_1 = $scope.simulateSystems.measuringStations.length; indexMS_1 < lenMS_1; ++indexMS_1) {
                if ($scope.addVar.msId === $scope.simulateSystems.measuringStations[indexMS_1].id) {
                    $scope.addVar.maxFlowHolder = $scope.simulateSystems.measuringStations[indexMS_1].oilRemaining;
                }
            }
        };

        $scope.deleteFM = function () {
            httpReq.deleteFM($scope.deteleVar.msId, $scope.deteleVar.fmId)
                    .then(function (data) {
                        $scope.closeAlert($scope.alerts.deleteFMAlerts);
                        $scope.addAlert($scope.alerts.deleteFMAlerts, {type: 'success', msg: data.response});
                        $scope.status.isAddPanel = false;
                        $scope.status.isDeletePanel = false;
                        $scope.status.isAddDeletePanel = true;
                        $scope.queryElements();
                    }, function (error) {
                        $scope.closeAlert($scope.alerts.deleteFMAlerts);
                        $scope.addAlert($scope.alerts.deleteFMAlerts, {type: 'danger', msg: error.response});
                        console.log(error);
                    });
        };

        $scope.cancel = function () {
            $scope.closeAlert($scope.alerts.deleteFMAlerts);
            $scope.status.isAddPanel = false;
            $scope.status.isDeletePanel = false;
            $scope.status.isAddDeletePanel = true;
        };

        $scope.addFM = function () {
            if ($scope.addVar.flow !== 0.0 || $scope.addVar.flow !== null) {
                if ($scope.addVar.flow < $scope.addVar.maxFlowHolder) {
                    var dataObj = {
                        'nomFlow': $scope.addVar.flow
                    };
                    httpReq.createFM($scope.ids.luId, $scope.addVar.msId, dataObj)
                            .then(function (data) {
                                $scope.closeAlert($scope.alerts.deleteFMAlerts);
                                $scope.addAlert($scope.alerts.deleteFMAlerts, {type: 'success', msg: data.id});
                                $scope.status.isAddPanel = false;
                                $scope.status.isDeletePanel = false;
                                $scope.status.isAddDeletePanel = true;
                                $scope.queryElements();
                            }, function (error) {
                                $scope.closeAlert($scope.alerts.deleteFMAlerts);
                                $scope.addAlert($scope.alerts.deleteFMAlerts, {type: 'danger', msg: error.response});
                                console.log(error);
                            });
                } else {
                    $scope.closeAlert($scope.alerts.addFMAlerts);
                    $scope.addAlert($scope.alerts.addFMAlerts, {type: 'danger', msg: 'Flow : [' + $scope.addVar.flow + '] is more than oil flow Remaining : [' + $scope.addVar.maxFlowHolder + ']'});
                }
            } else {
                $scope.closeAlert($scope.alerts.addFMAlerts);
                $scope.addAlert($scope.alerts.addFMAlerts, {type: 'danger', msg: 'flow cannot be 0'});
            }
        };

        $scope.querySensors = function () {
            (function myLoop(index) {
                setTimeout(function () {
                    httpReq.queryLUSensor($scope.ids.luId)
                            .then(function (data) {
                                if ($scope.gaugeEnable) {
                                    $scope.luLevelGauge.value = data.levelSensorValue;
                                    $scope.luFilterGauge.value = parseFloat(data.filterCapacity);
                                    $scope.luInFlowGauge.value = data.inFlowSensorValue;
                                    $scope.luOutFlowGauge.value = data.outFlowSensorValue;
                                    $scope.luParticelBox.value = data.particleCount;
                                    $scope.luTempGauge.value = parseInt(data.temperatureSensorValue);
                                    $scope.luWaterGauge.value = parseInt(data.waterContentSensorValue);
                                }
                                data.inFlowSensorValue = parseFloat(data.inFlowSensorValue).toFixed(2);
                                data.outFlowSensorValue = parseFloat(data.outFlowSensorValue).toFixed(2);
                                data.levelSensorValue = parseFloat(data.levelSensorValue).toFixed(2);
                                data.filterCapacity = parseFloat(data.filterCapacity).toFixed(2);
                                if (data.isSimulated) {
                                    $scope.simulateSystems.controlStatus = 'Simulating';
                                    $scope.simulateSystems.controlStyle = {
                                        'background-color': '#00CC66'};
                                } else {
                                    $scope.simulateSystems.controlStatus = 'Paused';
                                    $scope.simulateSystems.controlStyle = {
                                        'background-color': '#FF5E5E'};
                                }
                                if (data.isRunning) {
                                    $scope.simulateSystems.machineStatus = 'ON';
                                    $scope.simulateSystems.machineStyle = {
                                        'background-color': '#00CC66'};
                                } else {
                                    $scope.simulateSystems.machineStatus = 'OFF';
                                    $scope.simulateSystems.machineStyle = {
                                        'background-color': '#FF5E5E'};
                                }
                                $scope.queryDetails.luSensors = data;
                            }, function (error) {
                                console.log(error);
                                $scope.simulateSystems.querySensors = 0;
                            });
                    for (index = 0, len = $scope.simulateSystems.measuringStations.length; index < len; ++index) {
                        httpReq.queryMSSensor($scope.simulateSystems.measuringStations[index].id)
                                .then(function (data) {
                                    if (($scope.simulateSystems.gaugeMS !== '') && ($scope.simulateSystems.gaugeMS === data.id) && ($scope.gaugeEnable)) {
                                        $scope.msFlowGauge.value = data.outFlowSensorValue;
                                        $scope.msTempGauge.value = data.temperatureSensorValue;
                                        $scope.msPresGauge.value = data.pressureSensorValue;
                                    }
                                    data.inFlowSensorValue = data.inFlowSensorValue.toFixed(2);
                                    data.outFlowSensorValue = data.outFlowSensorValue.toFixed(2);
                                    if ($scope.queryDetails.msSensors.length === 0) {
                                        $scope.queryDetails.msSensors.push(data);
                                    } else {
                                        tempFlag = 0;
                                        for (index_1 = 0, len_1 = $scope.queryDetails.msSensors.length; index_1 < len_1; ++index_1) {
                                            if ($scope.queryDetails.msSensors[index_1].id === data.id) {
                                                $scope.queryDetails.msSensors[index_1] = data;
                                                tempFlag = 1;
                                            }
                                        }
                                        if (tempFlag === 0) {
                                            $scope.queryDetails.msSensors.push(data);
                                        }
                                    }
                                }, function (error) {
                                    console.log(error);
                                    $scope.simulateSystems.querySensors = 0;
                                });
                    }
                    for (index = 0, len = $scope.simulateSystems.flowMeters.length; index < len; ++index) {
                        httpReq.queryFMSensor($scope.simulateSystems.flowMeters[index].id)
                                .then(function (data) {
                                    if (($scope.gaugeEnable)) {
                                        for (indexFM = 0, lenFM = $scope.fmFlowGauges.length; indexFM < lenFM; ++indexFM) {
                                            if ($scope.fmFlowGauges[indexFM].id === data.id) {
                                                $scope.fmFlowGauges[indexFM].value = data.outFlowSensorValue;
                                                console.log(data.outFlowSensorValue);
                                            }
                                        }
                                    }
                                    data.inFlowSensorValue = data.inFlowSensorValue.toFixed(2);
                                    data.outFlowSensorValue = data.outFlowSensorValue.toFixed(2);
                                    if ($scope.queryDetails.fmSensors.length === 0) {
                                        $scope.queryDetails.fmSensors.push(data);
                                    } else {
                                        tempFlag = 0;
                                        for (index_1 = 0, len_1 = $scope.queryDetails.fmSensors.length; index_1 < len_1; ++index_1) {
                                            if ($scope.queryDetails.fmSensors[index_1].id === data.id) {
                                                $scope.queryDetails.fmSensors[index_1] = data;
                                                tempFlag = 1;
                                            }
                                        }
                                        if (tempFlag === 0) {
                                            $scope.queryDetails.fmSensors.push(data);
                                        }
                                    }
                                }, function (error) {
                                    console.log(error);
                                    $scope.simulateSystems.querySensors = 0;
                                });
                    }
                    $scope.tempFMsensors = [];
                    for (indexMS_1 = 0, lenMS_1 = $scope.simulateSystems.measuringStations.length; indexMS_1 < lenMS_1; ++indexMS_1) {
                        dummy.currentFM.length = 0;
                        dummy.currentFM = Object.keys($scope.simulateSystems.measuringStations[indexMS_1].flowMeters);
                        tempArrayFMSensor = [];
                        for (indexFM = 0, lenFM = dummy.currentFM.length; indexFM < lenFM; ++indexFM) {
                            for (indexFM_1 = 0, lenFM_1 = $scope.queryDetails.fmSensors.length; indexFM_1 < lenFM_1; ++indexFM_1) {
                                if (String($scope.queryDetails.fmSensors[indexFM_1].id) === String(dummy.currentFM[indexFM])) {
                                    tempArrayFMSensor.push($scope.queryDetails.fmSensors[indexFM_1]);
                                }
                            }
                        }
                        id = $scope.simulateSystems.measuringStations[indexMS_1].id;
                        $scope.tempFMsensors.push({"id": id, "flowMeters": tempArrayFMSensor});
                    }
                    $scope.simulateSystems.msSensors = $scope.tempFMsensors;

                    if ($scope.simulateSystems.selMSId !== '') {
                        for (indexMS_1 = 0, lenMS_1 = $scope.simulateSystems.msSensors.length; indexMS_1 < lenMS_1; ++indexMS_1) {
                            if ($scope.simulateSystems.selMSId === $scope.simulateSystems.msSensors[indexMS_1].id) {
                                $scope.simulateSystems.selFMSensors = $scope.simulateSystems.msSensors[indexMS_1].flowMeters;
                            }
                        }
                    }

                    if ($scope.simulateSystems.querySensors === 1) {
                        myLoop(index);
                    }

                }, 2000);
            })($scope.simulateSystems.querySensors);
        };

        $scope.generateFMSensors = function () {
            if ($scope.simulateSystems.selMSId !== '') {
                for (indexMS_1 = 0, lenMS_1 = $scope.simulateSystems.msSensors.length; indexMS_1 < lenMS_1; ++indexMS_1) {
                    if ($scope.simulateSystems.selMSId === $scope.simulateSystems.msSensors[indexMS_1].id) {
                        $scope.simulateSystems.selFMSensors = $scope.simulateSystems.msSensors[indexMS_1].flowMeters;
                    }
                }
            }
        };

        $scope.fastForward = function () {
            if ($scope.time.iteration !== '') {
                time = 0;
                time = parseInt($scope.time.iteration.substring(0, $scope.time.iteration.indexOf('x')));
                httpReq.forwardTime($scope.ids.luId, $scope.time.mode, time)
                        .then(function (data) {
                            console.log(data.response);
                            $scope.closeAlert($scope.alerts.stepAlerts);
                            $scope.addAlert($scope.alerts.stepAlerts, {type: 'success', msg: data.response});
                        }, function (error) {
                            $scope.closeAlert($scope.alerts.stepAlerts);
                            $scope.addAlert($scope.alerts.stepAlerts, {type: 'danger', msg: error.response});
                        });
            }
        };

        $scope.getIP = function () {
            httpReq.getURLs()
                    .then(function (data) {
                        $scope.myUrl = data.response;
                    }, function (error) {
                        console.log(error);
                    });
        };

        $scope.forwardRTU = function () {
            $scope.ids.lsId = $scope.displaySystem.lubricationUnit.parentId;
            console.log($scope.myUrl);
            $window.open($scope.myUrl + '/' + $scope.ids.lsId + '/RTU', '_blank');
        };

        $scope.startSimulation = function () {
            httpReq.simulateOrStopLU($scope.ids.luId, "simulate")
                    .then(function (data) {
                        $scope.simulateSystems.querySensors = 1;
                        $scope.simulateSystems.controlAlertStatus = false;
                        $scope.status.isAddDeletePanel = false;
                        $scope.status.isAddPanel = false;
                        $scope.status.isDeletePanel = false;
                        $scope.status.isAddDeleteFlowmeterButton = true;
                        $scope.querySensors();
                    }, function (error) {
                        console.log(error);
                    });
        };

        $scope.pauseSimulation = function () {
            httpReq.simulateOrStopLU($scope.ids.luId, "pause")
                    .then(function (data) {
                        $scope.simulateSystems.querySensors = 0;
                    }, function (error) {
                        console.log(error);
                    });
        };

        $scope.resetSimulation = function () {
            httpReq.simulateOrStopLU($scope.ids.luId, "reset")
                    .then(function (data) {
                        $scope.simulateSystems.controlAlertStatus = true;
                        $scope.simulateSystems.controlAlert = 'Please! Start the simulation again by pressing "Start" button';
                    }, function (error) {
                        console.log(error);
                    });
        };

        // Measuring Station tab
        $scope.generateMSGauge = function () {
            $scope.fmFlowGauges.length = 0;
            if (($scope.simulateSystems.gaugeMS !== '') && ($scope.gaugeEnable)) {
                if (($scope.displaySystem.lubricationUnit.length !== 0 || $scope.displaySystem.lubricationUnit !== '' || $scope.displaySystem.lubricationUnit !== undefined || $scope.displaySystem.lubricationUnit !== null)) {
                    dummy.currentMS.length = 0;
                    dummy.currentMS = Object.keys($scope.displaySystem.lubricationUnit.measuringStations);
                    for (indexMS = 0, lenMS = dummy.currentMS.length; indexMS < lenMS; ++indexMS) {
                        if (dummy.currentMS[indexMS] === $scope.simulateSystems.gaugeMS) {
                            $scope.changeMSFlowGauge($scope.displaySystem.lubricationUnit.measuringStations[dummy.currentMS[indexMS]].oilAllocation);
                            dummy.currentFM.length = 0;
                            dummy.currentFM = Object.keys($scope.displaySystem.lubricationUnit.measuringStations[dummy.currentMS[indexMS]].flowMeters);
                            for (indexFM = 0, lenFM = dummy.currentFM.length; indexFM < lenFM; ++indexFM) {
                                tempFMFlow = $scope.displaySystem.lubricationUnit.measuringStations[dummy.currentMS[indexMS]].flowMeters[dummy.currentFM[indexFM]].nomFlow;
                                $scope.fmFlowGauges.push({
                                    id: dummy.currentFM[indexFM],
                                    value: 0,
                                    upperLimit: tempFMFlow,
                                    lowerLimit: 0,
                                    unit: "(l/min)",
                                    precision: 2,
                                    ranges: [
                                        {
                                            min: 0,
                                            max: tempFMFlow * 0.25,
                                            color: '#C50200'
                                        },
                                        {
                                            min: tempFMFlow * 0.25,
                                            max: tempFMFlow * 0.50,
                                            color: '#FF7700'
                                        },
                                        {
                                            min: tempFMFlow * 0.50,
                                            max: tempFMFlow * 0.75,
                                            color: '#FDC702'
                                        },
                                        {
                                            min: tempFMFlow * 0.75,
                                            max: tempFMFlow,
                                            color: '#8DCA2F'
                                        }
                                    ]
                                });
                            }
                        }
                    }
                }
            }
        };

        $scope.activateDisp = function () {
            if ($scope.switch === 'off') {
                $scope.queryElements();
                $scope.simulateSystems.tableShow = false;
                $scope.gaugeEnable = true;
            } else if ($scope.switch === 'on') {
                $scope.simulateSystems.tableShow = true;
            }
        };


        //------------------------------Gauges change --------------------------

        $scope.changeluLevelGauge = function (upper) {
            $scope.luLevelGauge.upperLimit = upper;
            $scope.luLevelGauge.ranges = [
                {
                    min: 0,
                    max: parseInt($scope.luLevelGauge.upperLimit * 0.3),
                    color: '#C50200'
                },
                {
                    min: parseInt($scope.luLevelGauge.upperLimit * 0.3),
                    max: parseInt($scope.luLevelGauge.upperLimit * 0.5),
                    color: '#FF7700'
                },
                {
                    min: parseInt($scope.luLevelGauge.upperLimit * 0.5),
                    max: parseInt($scope.luLevelGauge.upperLimit * 0.7),
                    color: '#FDC702'
                },
                {
                    min: parseInt($scope.luLevelGauge.upperLimit * 0.7),
                    max: parseInt($scope.luLevelGauge.upperLimit),
                    color: '#8DCA2F'
                }
            ];
        };

        $scope.changeluFilterGauge = function (upper) {
            $scope.luFilterGauge.upperLimit = upper;
            $scope.luFilterGauge.ranges = [
                {
                    min: 0,
                    max: parseInt($scope.luFilterGauge.upperLimit * 0.3),
                    color: '#C50200'
                },
                {
                    min: parseInt($scope.luFilterGauge.upperLimit * 0.3),
                    max: parseInt($scope.luFilterGauge.upperLimit * 0.5),
                    color: '#FF7700'
                },
                {
                    min: parseInt($scope.luFilterGauge.upperLimit * 0.5),
                    max: parseInt($scope.luFilterGauge.upperLimit * 0.7),
                    color: '#FDC702'
                },
                {
                    min: parseInt($scope.luFilterGauge.upperLimit * 0.7),
                    max: parseInt($scope.luFilterGauge.upperLimit),
                    color: '#8DCA2F'
                }
            ];
        };

        $scope.changeluFlowGauge = function (upper) {
            $scope.luInFlowGauge.upperLimit = upper;
            $scope.luInFlowGauge.ranges = [
                {
                    min: 0,
                    max: parseInt($scope.luInFlowGauge.upperLimit * 0.75),
                    color: '#8DCA2F'
                },
                {
                    min: parseInt($scope.luInFlowGauge.upperLimit * 0.75),
                    max: parseInt($scope.luInFlowGauge.upperLimit * 0.9),
                    color: '#FF7700'
                },
                {
                    min: parseInt($scope.luInFlowGauge.upperLimit * 0.9),
                    max: parseInt($scope.luInFlowGauge.upperLimit),
                    color: '#C50200'
                }
            ];

            $scope.luOutFlowGauge.upperLimit = upper;
            $scope.luOutFlowGauge.ranges = [
                {
                    min: 0,
                    max: parseInt($scope.luOutFlowGauge.upperLimit * 0.75),
                    color: '#8DCA2F'
                },
                {
                    min: parseInt($scope.luOutFlowGauge.upperLimit * 0.75),
                    max: parseInt($scope.luOutFlowGauge.upperLimit * 0.9),
                    color: '#FF7700'
                },
                {
                    min: parseInt($scope.luOutFlowGauge.upperLimit * 0.9),
                    max: parseInt($scope.luOutFlowGauge.upperLimit),
                    color: '#C50200'
                }
            ];
        };

        $scope.changeMSFlowGauge = function (upper) {
            $scope.msFlowGauge.upperLimit = upper;
            $scope.msFlowGauge.ranges = [
                {
                    min: 0,
                    max: parseInt($scope.msFlowGauge.upperLimit * 0.3),
                    color: '#C50200'
                },
                {
                    min: parseInt($scope.msFlowGauge.upperLimit * 0.3),
                    max: parseInt($scope.msFlowGauge.upperLimit * 0.5),
                    color: '#FF7700'
                },
                {
                    min: parseInt($scope.msFlowGauge.upperLimit * 0.5),
                    max: parseInt($scope.msFlowGauge.upperLimit * 0.7),
                    color: '#FDC702'
                },
                {
                    min: parseInt($scope.msFlowGauge.upperLimit * 0.7),
                    max: parseInt($scope.msFlowGauge.upperLimit),
                    color: '#8DCA2F'
                }
            ];
        };

        //------------------------------Operator Panel--------------------------

        $scope.operatorEnableClick = function () {
            if ($scope.screen.simulateScreen) {
                $scope.setScreenFalse();
                $scope.screen.operatorScreen = true;
                $scope.simulateSystems.querySensors = 0;
                $scope.operatorPanel.queryHMI = 1;
                $scope.queryHMI();
            } else {
                $scope.setScreenFalse();
                $scope.screen.simulateScreen = true;
                $scope.operatorPanel.queryHMI = 0;
                $scope.simulateSystems.querySensors = 1;
                $scope.querySensors();
            }
        };

        $scope.queryHMI = function () {
            (function myLoop(index) {
                setTimeout(function () {
                    httpReq.queryHMI($scope.ids.luId)
                            .then(function (data) {
                                if (data.isRunning) {
                                    $scope.operatorPanel.machineLedStyle = $scope.operatorPanel.greenBurningStyle;
                                } else {
                                    $scope.operatorPanel.machineLedStyle = $scope.operatorPanel.redBurningStyleMachine;
                                }
                                if (data.oilChange) {
                                    $scope.operatorPanel.oilLedStyle = $scope.operatorPanel.redAnimatesBurningStyle;
                                } else {
                                    $scope.operatorPanel.oilLedStyle = $scope.operatorPanel.redBurningStyleSensor;
                                }
                                if (data.filterChange) {
                                    $scope.operatorPanel.filterLedStyle = $scope.operatorPanel.redAnimatesBurningStyle;
                                } else {
                                    $scope.operatorPanel.filterLedStyle = $scope.operatorPanel.redBurningStyleSensor;
                                }
                                $scope.operatorPanel.simTime = data.simTime;
                                $scope.operatorPanel.machineOn = data.isRunning;
                                $scope.operatorPanel.messages = data.messages;
                                console.log($scope.operatorPanel.messages);
                            }, function (error) {
                                console.log(error);
                                $scope.operatorPanel.queryHMI = 0;
                            });
                    if ($scope.operatorPanel.queryHMI === 1) {
                        myLoop(index);
                    }
                }, 200);
            })($scope.operatorPanel.queryHMI);
        };

        $scope.onLU = function () {
            httpReq.luMode($scope.ids.luId, "on")
                    .then(function (data) {
                        console.log(data);
                    }, function (error) {
                        console.log(error);
                    });
        };

        $scope.offLU = function () {
            httpReq.luMode($scope.ids.luId, "off")
                    .then(function (data) {
                        console.log(data);
                    }, function (error) {
                        console.log(error);
                    });
        };

        $scope.changeOil = function () {
            httpReq.maintenanceControls($scope.ids.luId, "oilChange")
                    .then(function (data) {
                        $scope.closeAlert($scope.alerts.maintenanceAlerts);
                        $scope.addAlert($scope.alerts.maintenanceAlerts, {type: 'success', msg: data.response});
                    }, function (error) {
                        $scope.closeAlert($scope.alerts.maintenanceAlerts);
                        $scope.addAlert($scope.alerts.maintenanceAlerts, {type: 'danger', msg: error.response});
                    });
        };

        $scope.changeFilter = function () {
            httpReq.maintenanceControls($scope.ids.luId, "filterChange")
                    .then(function (data) {
                        $scope.closeAlert($scope.alerts.maintenanceAlerts);
                        $scope.addAlert($scope.alerts.maintenanceAlerts, {type: 'success', msg: data.response});
                    }, function (error) {
                        $scope.closeAlert($scope.alerts.maintenanceAlerts);
                        $scope.addAlert($scope.alerts.maintenanceAlerts, {type: 'danger', msg: error.response});
                    });
        };

        $scope.maintenanceDo = function () {
            httpReq.maintenanceControls($scope.ids.luId, "stopLeakage")
                    .then(function (data) {
                        $scope.closeAlert($scope.alerts.maintenanceAlerts);
                        $scope.addAlert($scope.alerts.maintenanceAlerts, {type: 'success', msg: data.response});
                    }, function (error) {
                        $scope.closeAlert($scope.alerts.maintenanceAlerts);
                        $scope.addAlert($scope.alerts.maintenanceAlerts, {type: 'danger', msg: error.response});
                    });
        };

    }]);

//Controller for screen popup
lubricationSystemApp.controller('ModalInstanceCtrl', function ($scope, $uibModalInstance, messages) {
    $scope.messages = messages;
    $scope.ok = function () {
        $uibModalInstance.close();
    };
});