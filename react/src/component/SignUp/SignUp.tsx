import { Button, Divider, Fab, IconButton, TextField } from "@mui/material";
import { observer, useMobxState } from "mobx-react-use-autorun";
import { FormattedMessage } from "react-intl";
import { stylesheet } from "typestyle";
import { v1 } from "uuid";
import Box from '@mui/material/Box';
import Stepper from '@mui/material/Stepper';
import Step from '@mui/material/Step';
import StepLabel from '@mui/material/StepLabel';
import { useMount } from "mobx-react-use-autorun";
import api from "@/api";
import { MessageService } from "@/common/MessageService";
import { UserEmailModel } from "@/model/UserEmailModel";
import { Link, useNavigate } from "react-router-dom";
import LoadingOrErrorComponent from "@/common/LoadingOrErrorComponent/LoadingOrErrorComponent";
import { ReactNode } from "react";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faFloppyDisk, faPaperPlane, faPlus, faSpinner, faTrash } from "@fortawesome/free-solid-svg-icons";

const css = stylesheet({
  container: {
    display: "flex",
    flexDirection: "column",
    alignItems: "center",
    flex: "1 1 auto",
    width: "100%",
    paddingLeft: "5em",
    paddingRight: "5em",
  }
})

export default observer(() => {

  const state = useMobxState({
    nickname: '',
    password: '',
    emailList: [] as UserEmailModel[],
    steps: [] as { id: string; text: ReactNode }[],
    activeStep: 0,
    submitted: false,
    loading: {
      signUp: false,
      sendVerificationCode: {} as Record<string, boolean>,
    },
    ready: false,
    errorOfInit: null as any,
    error: {
      nickname() {
        if (state.nickname) {
          if (state.nickname.replaceAll(new RegExp('^\\s+', 'g'), '').length !== state.nickname.length) {
            return <FormattedMessage id="ThereShouldBeNoSpacesAtTheBeginningOfTheNickname" defaultMessage="There should be no spaces at the beginning of the nickname" />
          }
          if (state.nickname.replaceAll(new RegExp('\\s+$', 'g'), '').length !== state.nickname.length) {
            return <FormattedMessage id="TheNicknameCannotHaveASpaceAtTheEnd" defaultMessage="The nickname cannot have a space at the end" />
          }
        }
        if (!state.submitted) {
          return false;
        }
        if (!state.nickname) {
          return <FormattedMessage id="PleaseFillInNickname" defaultMessage="Please fill in nickname" />
        }
        return false;
      },
      password() {
        if (state.password) {
          if (state.password.replaceAll(new RegExp('^\\s+', 'g'), '').length !== state.password.length) {
            return <FormattedMessage id="PasswordMustNotHaveSpacesAtTheBeginning" defaultMessage="Password must not have spaces at the beginning" />
          }
          if (state.password.replaceAll(new RegExp('\\s+$', 'g'), '').length !== state.password.length) {
            return <FormattedMessage id="PasswordCannotHaveASpaceAtTheEnd" defaultMessage="Password cannot have a space at the end" />
          }
        }
        if (!state.submitted) {
          return false;
        }
        if (!state.password) {
          return <FormattedMessage id="PleaseFillInThePassword" defaultMessage="Please fill in the password" />
        }
        return false;
      },
      email(emailInfo: UserEmailModel) {
        if (!state.submitted) {
          return false;
        }
        if (!emailInfo.email) {
          return <FormattedMessage id="PleaseEnterYourEmail" defaultMessage="Please enter your email" />
        }
        if (!new RegExp('^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$').test(emailInfo.email)) {
          return <FormattedMessage id="EMailFormatIsIncorrect" defaultMessage="E-mail format is incorrect" />
        }
        return false;
      },
      verificationCode(emailInfo: UserEmailModel) {
        if (!state.submitted) {
          return false;
        }
        if (!emailInfo.verificationCodeEmail.verificationCode) {
          return <FormattedMessage id="PleaseFillInTheVerificationCode" defaultMessage="Please fill in the verification code" />
        }
        return false;
      }
    },
  }, {
    navigate: useNavigate(),
  })

  useMount(async () => {
    try {
      await initSteps();
      state.ready = true;
    } catch (e) {
      state.errorOfInit = e;
    }
  })

  async function initSteps() {
    state.steps.splice(0, state.steps.length);
    state.steps.push({
      id: v1(),
      text: <FormattedMessage id="SetNickname" defaultMessage="Set nickname" />,
    });
    state.steps.push({
      id: v1(),
      text: <FormattedMessage id="SetPassword" defaultMessage="Set password" />,
    });
    state.steps.push({
      id: v1(),
      text: <FormattedMessage id="BindEmail" defaultMessage="Bind email" />,
    });
    state.steps.push({
      id: v1(),
      text: <FormattedMessage id="Complete" defaultMessage="Complete" />,
    });
  }

  async function signUp() {
    if (state.loading.signUp) {
      return;
    }

    try {
      state.loading.signUp = true
      const userEmailList: UserEmailModel[] = state.emailList.map(s => ({
        email: s.email,
        verificationCodeEmail: s.verificationCodeEmail
      }));
      await api.Authorization.signUp(state.password, state.nickname, userEmailList);
      state.navigate("/chat")
    } catch (e) {
      MessageService.error(e);
    } finally {
      state.loading.signUp = false
    }
  }
  function nextStep() {
    state.submitted = true;
    if (state.activeStep === 0 && state.error.nickname()) {
      return;
    }
    if (state.activeStep === 1 && state.error.password()) {
      return;
    }
    if (state.activeStep === 2 && state.emailList.some(s => state.error.email(s))) {
      return;
    }
    if (state.activeStep === 2 && state.emailList.some(s => state.error.verificationCode(s))) {
      return;
    }
    state.submitted = false

    if (state.activeStep < state.steps.length - 1) {
      state.activeStep++;
    }
  }

  return <LoadingOrErrorComponent error={state.errorOfInit} ready={state.ready}>
    <div className={css.container}>
      <div className="flex flex-col flex-auto w-full">
        <div className="flex flex-col flex-auto w-full">
          <div className="flex justify-center" style={{ marginTop: "1em" }}>
            <FormattedMessage id="SignUp" defaultMessage="SignUp" />
          </div>
          <Divider style={{ marginTop: "1em" }} />
          {state.activeStep === 0 && <div className="flex flex-col" style={{ marginTop: "1em" }}>
            <div>
              <FormattedMessage id="NicknameYouCanModifyYourNicknameAtAnyTime" defaultMessage="Nickname, you can modify your nickname at any time" />
            </div>
            <TextField
              label={<FormattedMessage id="Nickname" defaultMessage="nickname" />}
              variant="outlined"
              onChange={(e) => {
                state.nickname = e.target.value;
              }}
              value={state.nickname}
              autoComplete="off"
              error={!!state.error.nickname()}
              helperText={state.error.nickname()}
              style={{ marginTop: "1em" }}
              onKeyDown={(e) => {
                if (!e.shiftKey && e.key === "Enter") {
                  e.preventDefault()
                  nextStep();
                }
              }}
              autoFocus={true}
            />
          </div>}
          {state.activeStep !== 0 && (<div className="flex flex-row" style={{ marginTop: "1em" }}>
            <div style={{ marginRight: "1em" }}>
              <FormattedMessage id="Nickname" defaultMessage="nickname" />
              {":"}
            </div>
            <div>
              {state.nickname}
            </div>
          </div>)}
          {state.activeStep > 0 && <Divider style={{ marginTop: "1em", marginBottom: "1em" }} />}
          {state.activeStep > 1 && <div className="flex flex-row">
            <div className="flex flex-row" style={{ marginRight: "1em" }}>
              <FormattedMessage id="Password" defaultMessage="Password" />
              {":"}
            </div>
            <FormattedMessage id="PasswordSettingIsComplete" defaultMessage="Password setting is complete" />
          </div>}
          {state.activeStep === 1 && <div className="flex flex-col">
            <div className="flex" style={{ marginBottom: "0em" }} >
              <FormattedMessage id="JustLikeTheTreasureMapLetUsHideThePasswordInThisWorld" defaultMessage="Just like the treasure map, let's hide the password in this world. For example, select a paragraph as a password from Shakespeare's works." />
            </div>
            <div className="flex" style={{ marginBottom: "0em" }} >
              <FormattedMessage id="ThePasswordSupportsAllTheCharactersOfUTF8" defaultMessage="The password supports all the characters of UTF-8. You can use any of the language content you like as the password." />
            </div>
            <div className="flex" style={{ marginBottom: "0em" }}>
              <FormattedMessage id="WeDoNotProvideResetPasswordSoRememberYourPassword" defaultMessage="We do not provide reset password functions. So, remember your password." />
            </div>
            <div className="flex" style={{ marginBottom: "1em" }}>
              <FormattedMessage id="OnlyYouWhocanKnowThePassword" defaultMessage="We can't know your chat content and file content. Only you who can know the password." />
            </div>
            <TextField
              label={<FormattedMessage id="Password" defaultMessage="Password" />}
              className="flex flex-auto"
              variant="outlined"
              onChange={(e) => {
                state.password = e.target.value;
              }}
              inputProps={{
                style: {
                  resize: "vertical",
                }
              }}
              value={state.password}
              autoComplete="off"
              multiline={true}
              rows={6}
              error={!!state.error.password()}
              helperText={state.error.password()}
              autoFocus={true}
            />
          </div>}
          {state.activeStep > 1 && <Divider style={{ marginTop: "1em", marginBottom: "1em" }} />}
          {state.activeStep > 2 && <div>
            {state.emailList.length > 0 && <div className="flex flex-col">
              {state.emailList.map(s => <div key={s.id} className="flex flex-row" style={{ marginBottom: "1em" }}>
                <div className="flex flex-row" style={{ marginRight: "1em" }}>
                  <FormattedMessage id="Email" defaultMessage="Email" />
                  {":"}
                </div>
                {s.email}
              </div>)}
            </div>}
          </div>}
          {state.activeStep === 2 && <div className="flex flex-col">
            <div>
              <FormattedMessage id="BindedEmailCanSkip" defaultMessage="Binded email(Can Skip)" />
            </div>
            {state.emailList.map((s) => <div className="flex flex-row items-start" key={s.id} style={{ marginTop: '1em' }}>
              <TextField
                label={<FormattedMessage id="Email" defaultMessage="Email" />}
                variant="outlined"
                onChange={(e) => {
                  s.email = e.target.value;
                }}
                value={s.email}
                autoComplete="off"
                className="w-full"
                error={!!state.error.email(s)}
                helperText={state.error.email(s)}
                autoFocus={true}
              />
              <div style={{ height: "56px" }} className="flex items-center">
                <Button
                  style={{
                    marginLeft: "1em",
                    textTransform: "none",
                  }}
                  variant="contained"
                  onClick={async () => {
                    try {
                      state.submitted = true;
                      if (state.error.email(s)) {
                        return;
                      }
                      state.submitted = false;
                      state.loading.sendVerificationCode[s.id!] = true;
                      const { data } = await api.Authorization.sendVerificationCode(s.email);
                      s.verificationCodeEmail.id = data.id;
                      if (data.verificationCode && !s.verificationCodeEmail.verificationCode) {
                        s.verificationCodeEmail.verificationCode = data.verificationCode;
                      }
                      s.verificationCodeEmail.verificationCodeLength = data.verificationCodeLength;
                    } catch (e) {
                      MessageService.error(e);
                    } finally {
                      state.loading.sendVerificationCode[s.id!] = false;
                    }
                  }}
                  startIcon={<FontAwesomeIcon icon={state.loading.sendVerificationCode[s.id!] ? faSpinner : faPaperPlane} spin={state.loading.sendVerificationCode[s.id!]} style={{ fontSize: "medium" }} />}
                >
                  <FormattedMessage id="Send" defaultMessage="Send" />
                </Button>
              </div>
              <TextField
                label={<FormattedMessage id="VerificationCode" defaultMessage="Verification code" />}
                variant="outlined"
                onChange={(e) => {
                  if (e.target.value === '') {
                    s.verificationCodeEmail.verificationCode = e.target.value;
                  } else if (e.target.value && new RegExp("^[0-9]+$").test(e.target.value)) {
                    s.verificationCodeEmail.verificationCode = e.target.value;
                  }
                }}
                value={s.verificationCodeEmail.verificationCode}
                autoComplete="off"
                className="w-full"
                error={!!state.error.verificationCode(s)}
                helperText={state.error.verificationCode(s)}
                style={{ marginLeft: "1em" }}
              />
              <div style={{ height: "56px" }} className="flex items-center">
                <IconButton
                  aria-label="delete"
                  onClick={() => {
                    const index = state.emailList.findIndex(m => m.id === s.id);
                    if (index >= 0) {
                      state.emailList.splice(index, 1)
                    }
                  }}
                  style={{ marginLeft: "0.2em" }}
                >
                  <FontAwesomeIcon icon={faTrash} />
                </IconButton>
              </div>
            </div>)}
            <div style={{ marginTop: "0.9em" }}>
              <Fab
                color="primary"
                aria-label="add"
                size="small"
                onClick={() => {
                  state.emailList.push({
                    id: v1(),
                    email: '',
                    verificationCodeEmail: {
                      id: '',
                      verificationCode: '',
                      verificationCodeLength: 6,
                    }
                  })
                }}
              >
                <FontAwesomeIcon icon={faPlus} style={{ fontSize: "large" }} />
              </Fab>
            </div>
          </div>}
        </div>

        <Divider style={{ marginTop: "1em", marginBottom: "1em" }} />
        <div style={{ marginTop: "1em" }} className="flex flex-col">
          <Box sx={{ width: '100%' }}>
            <Stepper activeStep={state.activeStep} alternativeLabel>
              {state.steps.map((step) => (
                <Step key={step.id}>
                  <StepLabel>{step.text}</StepLabel>
                </Step>
              ))}
            </Stepper>
          </Box>
          <div className="flex justify-between" style={{ marginTop: "1em", marginBottom: "1em" }}>
            {state.activeStep === 0 && <div></div>}
            {state.activeStep > 0 && <Button
              style={{
                textTransform: "none"
              }}
              variant="contained"
              startIcon={<FontAwesomeIcon icon={faFloppyDisk} />}
              onClick={() => {
                if (state.activeStep > 0) {
                  state.activeStep--;
                }
              }}
            >
              <FormattedMessage id="Previous" defaultMessage="Previous" />
            </Button>}
            <Link to="/sign_in">
              <FormattedMessage id="SignIn" defaultMessage="SignIn" />
            </Link>
            {state.activeStep < state.steps.length - 1 && <Button
              variant="contained"
              startIcon={<FontAwesomeIcon icon={faFloppyDisk} />}
              onClick={nextStep}
              style={{
                textTransform: "none"
              }}
            >
              <FormattedMessage id="Next" defaultMessage="Next" />
            </Button>}
            {state.activeStep >= state.steps.length - 1 && <Button
              variant="contained"
              startIcon={<FontAwesomeIcon icon={state.loading.signUp ? faSpinner : faFloppyDisk} spin={state.loading.signUp} />}
              onClick={signUp}
              style={{
                textTransform: "none"
              }}
            >
              <FormattedMessage id="SignUp" defaultMessage="SignUp" />
            </Button>}
          </div>
        </div>
        <div>
        </div>
      </div>
    </div>
  </LoadingOrErrorComponent>
})