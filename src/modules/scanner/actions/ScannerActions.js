import Types from '../Types';

export const addRange = (id, text) => async (dispatch, getState) => {
    const {beaconsOnRange} = getState().RangeReducer;

    beaconsOnRange.push({id: id, text: text});
    dispatch({
        type: Types.ADD_BEACON_ON_RANGE,
        payload: beaconsOnRange.slice(),
    })
};

export const subsRange = (id) => async (dispatch, getState) => {
    const {beaconsOnRange} = getState().RangeReducer;
    let result;
    for (let i = 0; i < beaconsOnRange.length; i++) {
        if (beaconsOnRange[i].id === parseInt(id, 10)) {
            beaconsOnRange.splice(i, 1);
            break;
        }
    }
    dispatch({
        type: Types.SUBS_BEACON_ON_RANGE,
        payload: beaconsOnRange.slice(),
    })

};
