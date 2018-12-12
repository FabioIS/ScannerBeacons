import React from 'react';
import {View, Text} from 'react-native';



export const Baliza = (Props) => {
    const{
        id,
        text
    } = Props;

    return(
        <View>
            <Text>
                La baliza {text} está a una distancia de {id}.
            </Text>

        </View>
    )

};
