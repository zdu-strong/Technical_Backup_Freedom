import { observer, useMobxState } from "mobx-react-use-autorun";
import { stylesheet } from "typestyle";
import { useNavigate } from "react-router-dom";
import MessageChat from "@/component/Message/MessageChat";
import { useMount } from "mobx-react-use-autorun";
import api from "@/api";
import LoadingOrErrorComponent from "@/common/LoadingOrErrorComponent/LoadingOrErrorComponent";
import { v1 } from "uuid";
import MessageMenu from "@/component/Message/MessageMenu";
import MessageUnlimitedList from "@/component/Message/MessageUnlimitedList";
import { useRef } from "react";
import { concatMap, from, timer } from "rxjs";
import { GlobalUserInfo } from "@/common/Server";

const css = stylesheet({
  container: {
    width: "100%",
    height: "100%",
    flex: "1 1 auto",
    display: "flex",
    flexDirection: "column",
    alignItems: "center",
    justifyContent: "center",
    paddingLeft: "1em",
    paddingRight: "1em",
  },
})

export default observer(() => {

  const state = useMobxState({
    readyForStart: false,
    readyForMessageList: false,
    async setReadyForMessageList(readyForMessageList: boolean) {
      state.readyForMessageList = readyForMessageList;
    },
    error: null as any,
  }, {
    navigate: useNavigate(),
    variableSizeListRef: useRef<{
      scrollToItemByLast: () => Promise<void>,
    }>(),
  })

  useMount(async (subscription) => {
    subscription.add(timer(1).pipe(
      concatMap(() => from((async () => {
        try {
          if (!api.Authorization.isSignIn()) {
            await api.Authorization.signUp(v1(), "visitor", []);
          }
          state.readyForStart = true;
        } catch (error) {
          state.error = error;
        }
      })()))
    ).subscribe());
  })

  return <>
    <LoadingOrErrorComponent ready={state.readyForStart && state.readyForMessageList} error={state.error} />
    {
      state.readyForStart && <div className={css.container} style={state.readyForMessageList ? {} : { position: "absolute", visibility: "hidden" }} >
        <MessageMenu userId={GlobalUserInfo.id} username={GlobalUserInfo.username} />
        <MessageUnlimitedList
          userId={GlobalUserInfo.id!}
          username={GlobalUserInfo.username!}
          setReadyForMessageList={state.setReadyForMessageList}
          variableSizeListRef={state.variableSizeListRef}
          key={GlobalUserInfo.id}
        />
        <MessageChat
          userId={GlobalUserInfo.id!}
          username={GlobalUserInfo.username!}
          variableSizeListRef={state.variableSizeListRef}
        />
      </div>
    }
  </>
})