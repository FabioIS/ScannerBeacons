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
                Este es mi texto :{text}
            </Text>
            <Text>
                Este es mi id :{id}
            </Text>
        </View>
    )

};
