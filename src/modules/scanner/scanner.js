import React, {Component} from 'react';
import {StyleSheet, Text, View, Button, FlatList, TextInput, NativeModules} from 'react-native';
import {Actions} from 'react-native-router-flux';
import {connect} from "react-redux";
import {addRange, subsRange} from "./actions/ScannerActions";
import {Baliza} from "./elements/baliza"


class Scanner extends Component {

    constructor(props) {
        super(props);
        this.state = {text: 1};
        //setInterval(this.getBeaconsArray(), 2000)
    }

    rangeCounter = 0;
    beacons = [];
    distance = [];

    onClick() {
        console.log(this.state.text);
        this.textInput.clear();
        this.props.subsRange(this.state.text);
    }

    async getBeaconsArray() {
        try {
            console.log("Timer funciona");
            let {beacons, distance} = await NativeModules.BeaconModule.getBeaconsArray();
            this.beacons = beacons;
            this.distance = distance;
            console.log("Beacons recibidos: " + this.beacons);
        } catch (e) {
            console.error(e);
        }
    }


    render() {
        return (
            <View style={styles.mainContainer}>
                <View style={styles.containerTop}>
                    {this.props.x.beaconsOnRange.map(beacon => {
                        return (
                            <View style={styles.point}>
                                <Baliza id={beacon.id} text={beacon.text}/>
                            </View>
                        )
                    })}

                    {setInterval(this.getBeaconsArray(), 2000)}


                    {() => {
                        console.log("Estamos");
                        if (this.beacons.length !== 0 && this.distance.length !== 0) {
                            console.log("size > 0");
                            for (let i = 0; i < this.beacons.length; i++) {
                                let x = i;
                                return (
                                    <View style={styles.point}>
                                        <Baliza id={this.distance[x]} text={this.beacons[x]}/>
                                    </View>
                                )
                            }

                        }
                    }}


                    {/*<Button title="Click me"*/}
                    {/*onPress={() => this.props.addRange(this.rangeCounter++, "hola" + this.rangeCounter)}*/}
                    {/*/>*/}

                    {/*<TextInput*/}
                    {/*ref={input => {*/}
                    {/*this.textInput = input*/}
                    {/*}}*/}
                    {/*placeholder={"Escribeme"}*/}
                    {/*onChangeText={(text) => this.setState({text})}*/}

                    {/*/>*/}

                    {/*<Button title="Delete"*/}
                    {/*onPress={() => this.onClick()}*/}
                    {/*/>*/}
                </View>
                <View style={styles.containerDown}>

                    <Button title="Press me" onPress={() => NativeModules.BeaconModule.showToastMessage("Todo guay")}/>
                </View>
            </View>
        );
    }
}


/*    <FlatList
                    data={this.props.x.beaconsOnRange}
                    renderItem={({item}) => <Text> {item.text} </Text>}
                />
                <View> */

const styles = StyleSheet.create({
    mainContainer: {
        flex: 1,

    },
    containerTop: {
        flex: 7,
        backgroundColor: 'blue' //'#c2e6e6'
    },
    containerDown: {
        flex: 1,
        backgroundColor: 'red',
    },
    point: {
        flex: 1,
    },
    text: {
        fontSize: 30,
        color: 'white'
    }
});


const mapStateToProps = state => {
    return {
        x: state.RangeReducer

    }
};

const mapStateToPropsAction = {addRange, subsRange};


export default connect(mapStateToProps, mapStateToPropsAction)(Scanner);
