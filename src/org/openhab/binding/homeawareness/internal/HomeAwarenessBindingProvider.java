package org.openhab.binding.homeawareness.internal;

import org.openhab.core.binding.BindingConfig;
import org.openhab.core.events.EventPublisher;
import org.openhab.core.items.Item;
import org.openhab.core.library.types.DecimalType;
import org.openhab.model.item.binding.AbstractGenericBindingProvider;
import org.openhab.model.item.binding.BindingConfigParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.org.apache.xalan.internal.xsltc.compiler.util.StringType;

public class HomeAwarenessBindingProvider extends AbstractGenericBindingProvider
{
    private static final Logger LOGGER = LoggerFactory.getLogger(HomeAwarenessBindingProvider.class);
    private EventPublisher eventPublisher;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getBindingType()
    {
        return "hhb";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validateItemType(Item item, String bindingConfig)
            throws BindingConfigParseException
    {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void processBindingConfiguration(final String context, final Item item,
            final String bindingConfig) throws BindingConfigParseException
    {
        LOGGER.info(
                "Processing binding information in [{}] for item [{}] with binding configuration [{}]",
                new String[]
                { context, item.getName(), bindingConfig });

        super.processBindingConfiguration(context, item, bindingConfig);

        if (bindingConfig != null)
        {
            final BindingConfig config = parseBindingConfig(item, bindingConfig);
            if (config != null)
            {
                addBindingConfig(item, config);
            }
            else
            {
                LOGGER.warn(
                        "Binding configuration is invalid for item={}, skipping binding for this item",
                        item);
            }
        }
        else
        {
            LOGGER.warn("bindingConfig is NULL (item=" + item
                    + ") -> process bindingConfig aborted!");
        }
    }

    /**
     * Parse the binding string associated with Home Heartbeat items. The format
     * follows the general form "id=XXXXXXXXXXXXXXXXX", where "X" is a hexadecimal digit in the
     * sensor id, e.g. "id=000D6F00CAFEBABE".
     *
     * @param item
     * @param bindingConfig
     * @return
     */
    private BindingConfig parseBindingConfig(final Item item, final String bindingConfig)
    {
        final BindingConfig haBindingConfig;
        if (bindingConfig.trim().startsWith("id="))
        {
            final String sensorId = bindingConfig.trim().substring(2,
                    bindingConfig.trim().length());
            haBindingConfig = new HomeAwarenessBindingConfig(sensorId, item);
        }
        else
        {
            return null;
        }

        return haBindingConfig;
    }

    private class HomeAwarenessBindingConfig implements BindingConfig
    {
        private final String sensorId;
        private final Item item;

        public HomeAwarenessBindingConfig(final String sensorId, final Item item)
        {
            this.sensorId = sensorId;
            this.item = item;
        }

        public String getSensorId()
        {
            return sensorId;
        }

        public Item getItem()
        {
            return item;
        }
    }

    public void setEventPublisher(final EventPublisher eventPublisher)
    {
        this.eventPublisher = eventPublisher;
    }

    public void update(final String updatedValue)
    {
        LOGGER.info("Update listener called with value {}", updatedValue);

        if (eventPublisher != null && updatedValue != null)
        {
            final org.openhab.core.types.State newState;

            if (bindingConfig.item.getAcceptedDataTypes().contains(DecimalType.class))
            {
                newState = new DecimalType(updatedValue);
            }
            else if (bindingConfig.item.getAcceptedDataTypes().contains(StringType.class))
            {
                newState = new StringType(updatedValue);
            }
            else
            {
                return;
            }

            LOGGER.info("Posting update event for item {} with value {}", new String[]
            { bindingConfig.item.getName(), newState.toString() });

            eventPublisher.postUpdate(bindingConfig.item.getName(), newState);
        }
    }
}
