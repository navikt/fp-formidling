package no.nav.foreldrepenger.melding.dokumentproduksjon.v2;

import static no.nav.vedtak.sts.client.StsClientType.SECURITYCONTEXT_TIL_SAML;
import static no.nav.vedtak.sts.client.StsClientType.SYSTEM_SAML;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import no.nav.tjeneste.virksomhet.dokumentproduksjon.v2.binding.DokumentproduksjonV2;
import no.nav.vedtak.log.metrics.ReadinessAware;
import no.nav.vedtak.sts.client.StsClientType;
import no.nav.vedtak.sts.client.StsConfigurationUtil;

@Dependent
public class DokumentproduksjonConsumerProducer implements ReadinessAware {
    private DokumentproduksjonConsumerConfig consumerConfig;

    @Inject
    public void setConfig(DokumentproduksjonConsumerConfig consumerConfig) {
        this.consumerConfig = consumerConfig;
    }

    public DokumentproduksjonConsumer dokumentproduksjonConsumer() {
        DokumentproduksjonV2 port = wrapWithSts(consumerConfig.getPort(), SECURITYCONTEXT_TIL_SAML);
        return new DokumentproduksjonConsumerImpl(port);
    }

    DokumentproduksjonSelftestConsumer dokumentproduksjonSelftestConsumer() {
        DokumentproduksjonV2 port = wrapWithSts(consumerConfig.getPort(), SYSTEM_SAML);
        return new DokumentproduksjonSelftestConsumerImpl(port, consumerConfig.getEndpointUrl());
    }

    DokumentproduksjonV2 wrapWithSts(DokumentproduksjonV2 port, StsClientType samlTokenType) {
        return StsConfigurationUtil.wrapWithSts(port, samlTokenType);
    }

    @Override
    public boolean isReady() {
        try {
            dokumentproduksjonSelftestConsumer().ping();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
